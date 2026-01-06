package com.inkspire.ebookreader.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.data.database.model.BookEntity
import com.inkspire.ebookreader.data.database.model.ChapterContentEntity
import com.inkspire.ebookreader.data.database.model.TableOfContentEntity
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import com.inkspire.ebookreader.domain.repository.ImagePathRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import com.inkspire.ebookreader.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.resource.buffered
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.shared.util.use
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.streamer.parser.audio.AudioParser
import org.readium.r2.streamer.parser.epub.EpubParser
import org.readium.r2.streamer.parser.image.ImageParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.Result as ImportResult

class CBZImportWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val bookRepository: BookRepository by inject()
    private val tableOfContentsRepository: TableOfContentRepository by inject()
    private val chapterRepository: ChapterRepository by inject()
    private val imagePathRepository: ImagePathRepository by inject()

    private val md = MessageDigest.getInstance("MD5")
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = System.currentTimeMillis().toInt()
    private val completionNotificationId = notificationId + 1
    private var finalBookTitle: String = ""
    private var finalBookId: String = ""

    companion object {
        const val INPUT_URI_KEY = "input_uri"
        private const val PROGRESS_CHANNEL_ID = "book_import_progress_channel"
        private const val COMPLETION_CHANNEL_ID = "book_import_completion_channel"
        private const val MAX_BITMAP_DIMENSION = 2048
    }

    init {
        createNotificationChannel(
            PROGRESS_CHANNEL_ID,
            context.getString(R.string.channel_import_progress_name)
        )
        createNotificationChannel(
            COMPLETION_CHANNEL_ID,
            context.getString(R.string.channel_import_completion_name)
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val initialNotification = createProgressNotificationBuilder(
            fileName = finalBookTitle,
            message = context.getString(R.string.status_starting)
        ).build()
        setForeground(getForegroundInfoCompat(initialNotification))

        val processingResult = processEPUB(
            onProgress = { progress, chapterName ->
                updateProgressNotification(
                    fileName = finalBookTitle,
                    message = context.getString(R.string.status_processing_item_fmt, chapterName),
                    progress = progress
                )
            }
        )

        val isSuccess = processingResult.isSuccess
        val failureReason = if (!isSuccess) processingResult.exceptionOrNull()?.message else null

        sendCompletionNotification(isSuccess, finalBookTitle, failureReason)
        Result.success()
    }

    private suspend fun processEPUB(
        onProgress: suspend (progress: Int?, chapterName: String) -> Unit
    ): ImportResult<String> {
        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(
            contentResolver = context.contentResolver,
            httpClient = httpClient
        )
        val publicationOpener = PublicationOpener(
            publicationParser = DefaultPublicationParser(
                context = context,
                httpClient = httpClient,
                assetRetriever = assetRetriever,
                pdfFactory = null,
                additionalParsers = listOf(
                    EpubParser(),
                    AudioParser(
                        assetSniffer = assetRetriever
                    ),
                    ImageParser(
                        assetRetriever = assetRetriever
                    ),
                )
            )
        )
        val epubUriString = inputData.getString(EPUBImportWorker.INPUT_URI_KEY) ?: return ImportResult.failure(
            IOException("Fail to get input uri")
        )
        val uri = epubUriString.toUri()
        val url = uri.toAbsoluteUrl()
        val asset = assetRetriever.retrieve(url!!).getOrNull() ?: return ImportResult.failure(IOException("Fail to load ebook"))
        val publication = publicationOpener.open(asset, allowUserInteraction = true).getOrNull() ?: return ImportResult.failure(IOException("Fail to open ebook"))
        onProgress(null, context.getString(R.string.status_analyzing))

        val (title,authors) = readComicInfoXml(publication)
        finalBookTitle = title ?: publication.metadata.title ?: getDisplayNameFromUri(context, uri) ?: context.getString(R.string.placeholder_untitled)

        var currentChapter = ""
        val chapterMap = mutableMapOf<String, MutableList<String>>()
        val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp")
        publication.readingOrder.forEach { link ->
            val href = link.href.toString().trimStart('/')
            if (imageExtensions.any { href.endsWith(it, ignoreCase = true) }) {
                val parts = href.split("/")
                val chapterName = if (parts.size < 2) {
                    finalBookTitle
                } else {
                    parts[parts.size - 2]
                }
                if (chapterName != currentChapter) {
                    currentChapter = chapterName.replace("%20", " ")
                }
                val list = chapterMap.getOrPut(currentChapter) { mutableListOf() }
                list.add(href)
            }
        }
        val sortedChapterMap = chapterMap.entries
            .sortedBy { entry ->
                Regex("(\\d+)(?!.*\\d)").find(entry.key)?.value?.toIntOrNull() ?: Int.MAX_VALUE
            }
            .associate { it.toPair() }
        val processBookResult = processAndSaveBookInfo(
            epubUriString = epubUriString,
            publication = publication,
            tableOfContentSize = sortedChapterMap.size,
            author = authors,
        )
        if (processBookResult.isFailure) {
            return processBookResult
        }

        onProgress(null, context.getString(R.string.status_saving_content))
        val processContentResult = processAndSaveBookContent(
            publication = publication,
            chapterMap = sortedChapterMap,
            onProgress = onProgress
        )
        if (processContentResult.isFailure) {
            return processContentResult
        }

        return ImportResult.success(finalBookTitle)
    }

    private suspend fun processAndSaveBookContent(
        publication: Publication,
        chapterMap: Map<String, List<String>>,
        onProgress: suspend (progress: Int?, chapterName: String) -> Unit
    ): ImportResult<String> {
        var overallChapterIndex = 0
        val contentList = mutableListOf<String>()
        chapterMap.forEach { (chapterName, imagePaths) ->
            val progress = ((overallChapterIndex + 1).toFloat() / chapterMap.size * 100).toInt()
            onProgress(progress, chapterName)

            imagePaths.forEach { href->
                val imagePath = saveImageFromPublication(
                    imageElementHref = href,
                    publication = publication,
                    tocIndex = overallChapterIndex,
                    paragraphIndex = imagePaths.indexOf(href)
                )
                if (!imagePath.startsWith("error")) {
                    contentList.add(imagePath)
                }
            }
            imagePathRepository.saveImagePath(finalBookId, contentList)
            saveChapterInfo(finalBookId, chapterName, overallChapterIndex, contentList)
            contentList.clear()
            overallChapterIndex++
        }
        return ImportResult.success(finalBookTitle)
    }

    @OptIn(ExperimentalReadiumApi::class)
    private suspend fun saveImageFromPublication(
        imageElementHref: String,
        publication: Publication,
        tocIndex: Int,
        paragraphIndex: Int,
    ): String {
        var imagePath = "error"
        publication.get(Url(imageElementHref)!!)?.buffered()?.use { buffering ->
            buffering.read().onSuccess { byteArray ->
                byteArray.inputStream().use {
                    val bitmap = BitmapUtil.decodeSampledBitmapFromStream(
                        it,
                        MAX_BITMAP_DIMENSION,
                        MAX_BITMAP_DIMENSION
                    )
                    if (bitmap != null) {
                        imagePath = BitmapUtil.saveBitmapToPrivateStorage(
                            context = context,
                            bitmap = bitmap,
                            compressType = Bitmap.CompressFormat.JPEG,
                            quality = 80,
                            filenameWithoutExtension = "image_${finalBookId}_${tocIndex}_${paragraphIndex}"
                        )
                        bitmap.recycle()
                    }
                }
            }
        }
        return imagePath
    }

    private suspend fun processAndSaveBookInfo(
        epubUriString: String,
        publication: Publication,
        tableOfContentSize: Int,
        author: String?,
    ): ImportResult<String> {
        finalBookId = BigInteger(1, md.digest(finalBookTitle.toByteArray())).toString(16)
            .padStart(32, '0')
        if (bookRepository.isBookExist(finalBookTitle)) {
            return ImportResult.failure(IOException("Book already imported"))
        }
        var coverImagePath: String
        val coverImageBitmap = publication.cover()
        coverImagePath = coverImageBitmap?.let { bitmap ->
            BitmapUtil.saveBitmapToPrivateStorage(
                context = context,
                bitmap = bitmap,
                compressType = Bitmap.CompressFormat.JPEG,
                quality = 80,
                filenameWithoutExtension = "cover_$finalBookId"
            )
        } ?: "error"
        val authors = author?.split(",") ?: publication.metadata.authors.map { it.name }
        bookRepository.insertBook(
            BookEntity(
                bookId = finalBookId,
                title = finalBookTitle,
                coverImagePath = coverImagePath,
                authors = authors,
                description = publication.metadata.description,
                totalChapter = tableOfContentSize,
                currentChapter = 0,
                currentParagraph = 0,
                isFavorite = false,
                storagePath = epubUriString,
                isEditable = false,
                fileType = "cbz"
            )
        ).run {
            bookRepository.updateRecentRead(finalBookId)
        }
        imagePathRepository.saveImagePath(finalBookId, listOf(coverImagePath))
        return ImportResult.success(finalBookTitle)
    }

    private suspend fun saveChapterInfo(
        bookId: String,
        chapterName: String,
        chapterIndex: Int,
        savedImagePaths: List<String>
    ) {
        val tocEntity = TableOfContentEntity(
            bookId = bookId,
            title = chapterName,
            index = chapterIndex
        )
        tableOfContentsRepository.saveTableOfContent(tocEntity)
        val chapterEntity = ChapterContentEntity(
            tocId = chapterIndex,
            bookId = bookId,
            chapterTitle = chapterName,
            content = savedImagePaths,
        )
        chapterRepository.saveChapterContent(chapterEntity)
    }

    /** Gets display name from content URI. */
    private fun getDisplayNameFromUri(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        var displayName: String? = null
        try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (_: Exception) {}
        return displayName
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val importance =
                if (channelId == PROGRESS_CHANNEL_ID) NotificationManager.IMPORTANCE_LOW else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                if (channelId == PROGRESS_CHANNEL_ID) {
                    setSound(null, null)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createProgressNotificationBuilder(
        fileName: String,
        message: String
    ): NotificationCompat.Builder {
        val displayFileName = fileName.substringBeforeLast(".")
        return NotificationCompat.Builder(context, PROGRESS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_title_importing_fmt, displayFileName.take(40) + if (displayFileName.length > 40) "..." else ""))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
    }

    private suspend fun updateProgressNotification(
        fileName: String,
        message: String,
        progress: Int?
    ) {
        val builder = createProgressNotificationBuilder(fileName, message)
        if (progress != null) {
            builder.setProgress(100, progress.coerceIn(0, 100), false)
        } else {
            builder.setProgress(0, 0, true)
        }
        try {
            setForeground(getForegroundInfoCompat(builder.build()))
        } catch (_: Exception) {
            notificationManager.notify(notificationId, builder.build())
        }
    }


    /** Sends the final completion notification. */
    private fun sendCompletionNotification(
        isSuccess: Boolean,
        bookTitle: String?,
        failureReason: String? = null
    ) {
        val title = if (isSuccess) context.getString(R.string.result_success_title) else context.getString(R.string.result_failed_title)
        val defaultTitle = bookTitle ?: context.getString(R.string.error_default_title)
        val userFriendlyReason = when {
            failureReason == null -> null
            failureReason.contains("Book already imported") -> context.getString(R.string.error_book_already_exists)
            failureReason.contains("No valid image entries found") -> context.getString(R.string.error_no_content)
            failureReason.contains("Failed to open InputStream") -> context.getString(R.string.error_read_file)
            failureReason.contains("OutOfMemoryError") -> context.getString(R.string.error_out_of_memory)
            else -> context.getString(R.string.error_unexpected)
        }

        val text = when {
            isSuccess -> context.getString(R.string.result_success_msg_fmt, defaultTitle)
            userFriendlyReason != null -> context.getString(R.string.error_detailed_fmt, defaultTitle, userFriendlyReason)
            else -> context.getString(R.string.error_generic_fmt, defaultTitle)
        }
        val builder = NotificationCompat.Builder(context, COMPLETION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        notificationManager.notify(completionNotificationId, builder.build())
        notificationManager.cancel(notificationId)
    }

    /** Provides ForegroundInfo, handling platform differences. */
    private fun getForegroundInfoCompat(notification: Notification): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }


    @OptIn(InternalReadiumApi::class)
    suspend fun readComicInfoXml(publication: Publication?):Pair<String?,String?> {
        val comicInfoHref = "ComicInfo.xml"
        val url = Url(comicInfoHref)

        val resource = publication?.container?.get(url!!) ?: return Pair(null, null)

        val result = resource.read()
        result.onSuccess { byteArray ->
            val parserFactory = XmlPullParserFactory.newInstance()
            parserFactory.isNamespaceAware = true
            val parser = parserFactory.newPullParser()

            parser.setInput(ByteArrayInputStream(byteArray), null)

            var eventType = parser.eventType
            var series: String? = null
            var writer: String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "Series","series" -> {
                                series = parser.nextText()?.trim()
                            }

                            "writer","Writer" -> {
                                writer = parser.nextText()?.trim()
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
            return Pair(series,writer)
        }.onFailure { _ ->
            return Pair(null,null)
        }
        return Pair(null,null)
    }
}