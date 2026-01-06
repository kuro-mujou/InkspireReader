package com.inkspire.ebookreader.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.ScrapedChapterRef
import com.inkspire.ebookreader.util.TruyenFullScraper
import com.inkspire.ebookreader.data.database.model.BookEntity
import com.inkspire.ebookreader.data.database.model.ChapterContentEntity
import com.inkspire.ebookreader.data.database.model.TableOfContentEntity
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import com.inkspire.ebookreader.domain.repository.ImagePathRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import com.inkspire.ebookreader.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import kotlin.random.Random

class TruyenFullImportWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

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
        const val INPUT_BOOK_URL_KEY = "input_url"
        const val INPUT_BOOK_TITLE_KEY = "input_title"
        const val INPUT_BOOK_AUTHOR_KEY = "input_author"
        const val INPUT_BOOK_DESCRIPTION_KEY = "input_description"
        const val INPUT_BOOK_CATEGORY_KEY = "input_categories"
        const val INPUT_BOOK_COVER_URL_KEY = "input_cover_url"

        private const val PROGRESS_CHANNEL_ID = "book_import_progress_channel"
        private const val COMPLETION_CHANNEL_ID = "book_import_completion_channel"
        private const val MAX_BITMAP_DIMENSION = 2048
    }

    init {
        createNotificationChannel(PROGRESS_CHANNEL_ID, "Book Import Progress")
        createNotificationChannel(COMPLETION_CHANNEL_ID, "Book Import Completion")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val bookUrl = inputData.getString(INPUT_BOOK_URL_KEY) ?: return@withContext Result.failure()
        finalBookTitle = inputData.getString(INPUT_BOOK_TITLE_KEY) ?: context.getString(R.string.placeholder_untitled)

        val author = inputData.getString(INPUT_BOOK_AUTHOR_KEY) ?: context.getString(R.string.placeholder_author_unknown)
        val description = inputData.getString(INPUT_BOOK_DESCRIPTION_KEY)
        val categoryString = inputData.getString(INPUT_BOOK_CATEGORY_KEY) ?: ""
        val coverUrl = inputData.getString(INPUT_BOOK_COVER_URL_KEY) ?: ""

        finalBookId = BigInteger(1, md.digest(finalBookTitle.toByteArray())).toString(16).padStart(32, '0')

        val initialNotification = createProgressNotificationBuilder(
            fileName = finalBookTitle,
            message = context.getString(R.string.status_starting)
        ).build()
        setForeground(getForegroundInfoCompat(initialNotification))

        try {
            updateProgressNotification(finalBookTitle, context.getString(R.string.status_analyzing), 0)
            val remoteTOC = TruyenFullScraper.fetchTOC(bookUrl)

            if (remoteTOC.isEmpty()) {
                sendCompletionNotification(false, finalBookTitle, context.getString(R.string.error_no_content))
                return@withContext Result.failure()
            }

            val existingBook = bookRepository.getBook(finalBookId)

            if (existingBook != null) {
                val localTotal = existingBook.totalChapter

                if (remoteTOC.size > localTotal) {
                    val newChaptersCount = remoteTOC.size - localTotal
                    updateProgressNotification(finalBookTitle, context.resources.getQuantityString(
                        R.plurals.status_found_new_chapters,
                        newChaptersCount,
                        newChaptersCount
                    ), 5)

                    val chaptersToDownload = remoteTOC.drop(localTotal)

                    chaptersToDownload.forEach { chapterRef ->
                        val tocEntity = TableOfContentEntity(
                            bookId = finalBookId,
                            title = chapterRef.title,
                            index = chapterRef.index
                        )
                        tableOfContentsRepository.saveTableOfContent(tocEntity)
                    }

                    processChapterRange(
                        chaptersToProcess = chaptersToDownload,
                        totalCountForProgress = chaptersToDownload.size,
                        isUpdateMode = true
                    )

                    bookRepository.saveBookInfoTotalChapter(finalBookId, remoteTOC.size)
                    bookRepository.updateRecentRead(finalBookId)

                    sendCompletionNotification(true, finalBookTitle, context.getString(R.string.result_success_update_fmt, finalBookTitle, newChaptersCount))
                } else {
                    sendCompletionNotification(true, finalBookTitle, context.getString(R.string.status_up_to_date))
                }

            } else {
                updateProgressNotification(finalBookTitle, context.getString(R.string.status_processing_cover), 0)

                val coverImagePath = downloadAndSaveImage(coverUrl, "cover_$finalBookId")

                val authorsList = listOf(author)
                bookRepository.insertBook(
                    BookEntity(
                        bookId = finalBookId,
                        title = finalBookTitle,
                        coverImagePath = coverImagePath,
                        authors = authorsList,
                        description = description,
                        totalChapter = remoteTOC.size,
                        currentChapter = 0,
                        currentParagraph = 0,
                        isFavorite = false,
                        storagePath = bookUrl,
                        isEditable = false,
                        fileType = "epub-online"
                    )
                ).run {
                    bookRepository.updateRecentRead(finalBookId)
                }
                imagePathRepository.saveImagePath(finalBookId, listOf(coverImagePath))

                val categories = categoryString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                categories.forEach { catName ->
                    val catToInsert = Category(id = 0, name = catName, color = Color.WHITE, isSelected = false)
                    val newId = bookRepository.insertCategory(catToInsert)
                    val catWithId = catToInsert.copy(id = newId.toInt())
                    bookRepository.addCategoryToBook(finalBookId, catWithId)
                }

                remoteTOC.forEach { chapterRef ->
                    val tocEntity = TableOfContentEntity(
                        bookId = finalBookId,
                        title = chapterRef.title,
                        index = chapterRef.index
                    )
                    tableOfContentsRepository.saveTableOfContent(tocEntity)
                }

                processChapterRange(
                    chaptersToProcess = remoteTOC,
                    totalCountForProgress = remoteTOC.size,
                    isUpdateMode = false
                )

                sendCompletionNotification(true, finalBookTitle)
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            sendCompletionNotification(false, finalBookTitle, e.message)
            Result.failure()
        }
    }

    private suspend fun processChapterRange(
        chaptersToProcess: List<ScrapedChapterRef>,
        totalCountForProgress: Int,
        isUpdateMode: Boolean
    ) {
        val total = totalCountForProgress.coerceAtLeast(1)

        for ((idx, chapterRef) in chaptersToProcess.withIndex()) {
            val progressPercentage = ((idx + 1).toFloat() / total * 100).toInt()
            val statusMsg = if (isUpdateMode)
                context.getString(R.string.notification_title_updating_fmt, chapterRef.title)
            else
                context.getString(R.string.status_processing_item_fmt, chapterRef.title)

            updateProgressNotification(finalBookTitle, statusMsg, progressPercentage)

            try {
                val rawHtml = TruyenFullScraper.fetchChapterContent(chapterRef.url)
                val document = Jsoup.parse(rawHtml)

                val (parsedContentList, imagePaths) = parseChapterHtmlSegment(
                    document = document,
                    chapterIndex = chapterRef.index
                )

                val contentToSave = mutableListOf<String>()
                contentToSave.add("<h2>${chapterRef.title}</h2>")
                if (parsedContentList.isNotEmpty()) {
                    contentToSave.addAll(parsedContentList)
                } else {
                    contentToSave.add(context.getString(R.string.error_processing_content))
                }

                chapterRepository.saveChapterContent(
                    ChapterContentEntity(
                        tocId = chapterRef.index,
                        bookId = finalBookId,
                        chapterTitle = chapterRef.title,
                        content = contentToSave
                    )
                )

                if (imagePaths.isNotEmpty()) {
                    imagePathRepository.saveImagePath(finalBookId, imagePaths)
                }

                delay(Random.nextLong(100, 500))

            } catch (e: Exception) {
                chapterRepository.saveChapterContent(
                    ChapterContentEntity(
                        tocId = chapterRef.index,
                        bookId = finalBookId,
                        chapterTitle = chapterRef.title,
                        content = listOf("<h2>${chapterRef.title}</h2>", context.getString(R.string.error_content_load_fmt, e.message))
                    )
                )
                e.printStackTrace()
            }
        }
    }

    private suspend fun downloadAndSaveImage(
        urlOrBase64: String,
        filenameWithoutExtension: String
    ): String = withContext(Dispatchers.IO) {
        if (urlOrBase64.isEmpty()) return@withContext "error_empty_url"

        try {
            var bitmap: Bitmap? = null

            if (urlOrBase64.startsWith("data:image/")) {
                try {
                    val commaIndex = urlOrBase64.indexOf(',')
                    if (commaIndex != -1) {
                        val base64Data = urlOrBase64.substring(commaIndex + 1)
                        val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    }
                } catch (_: Exception) {
                    return@withContext "error_base64_decode"
                }
            }
            else {
                val finalUrl = if (urlOrBase64.startsWith("//")) "https:$urlOrBase64" else urlOrBase64
                URL(finalUrl).openStream().use { stream ->
                    bitmap = BitmapUtil.decodeSampledBitmapFromStream(
                        stream,
                        MAX_BITMAP_DIMENSION,
                        MAX_BITMAP_DIMENSION
                    )
                }
            }

            bitmap?.let {
                val path = BitmapUtil.saveBitmapToPrivateStorage(
                    context = context,
                    bitmap = it,
                    compressType = Bitmap.CompressFormat.JPEG,
                    quality = 80,
                    filenameWithoutExtension = filenameWithoutExtension
                )
                it.recycle()
                return@withContext path
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext "error_download_failed"
    }

    private suspend fun parseChapterHtmlSegment(
        document: Document,
        chapterIndex: Int,
    ): Pair<List<String>, List<String>> {
        val contentList = mutableListOf<String>()
        val imagePaths = mutableListOf<String>()
        val currentParagraph = StringBuilder()
        var insideHeading = false
        var spaceAddedInHeading = false

        document.body().traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                when (node) {
                    is TextNode -> {
                        val text = node.text()
                        val textToAppend = text.replace(Regex("\\s+"), " ")
                        if (textToAppend.isNotBlank()) {
                            currentParagraph.append(textToAppend)
                        }
                    }

                    is Element -> {
                        when (val tagName = node.tagName().lowercase()) {
                            "p", "div", "ul", "ol", "li", "table", "blockquote", "hr",
                            "figure", "figcaption", "details", "summary", "main", "header",
                            "footer", "nav", "aside", "article", "section" -> {
                                if (!insideHeading) {
                                    flushParagraphWithFormatting(currentParagraph, contentList)
                                }
                            }

                            "h1", "h2", "h3", "h4", "h5", "h6" -> {
                                currentParagraph.append("<$tagName>")
                                insideHeading = true
                                spaceAddedInHeading = false
                            }

                            "br" -> {
                                if (insideHeading && !spaceAddedInHeading) {
                                    currentParagraph.append(" ")
                                    spaceAddedInHeading = true
                                } else if (!insideHeading) {
                                    flushParagraphWithFormatting(currentParagraph, contentList)
                                }
                            }

                            "b", "strong", "i", "em", "u", "s", "strike", "del", "sup", "sub", "code" -> {
                                currentParagraph.append("<$tagName>")
                            }

                            "td", "th" -> {
                                if (currentParagraph.isNotEmpty() && !currentParagraph.endsWith(' ')) {
                                    currentParagraph.append(" ")
                                }
                            }

                            "img", "image" -> {
                                if (!insideHeading) {
                                    flushParagraphWithFormatting(currentParagraph, contentList)
                                }

                                val srcAttr = when (tagName) {
                                    "img" -> {
                                        val dataSrc = node.attr("data-src")
                                        val src = node.attr("src")
                                        dataSrc.ifBlank { src }
                                    }
                                    "image" -> node.attr("xlink:href").ifEmpty { node.attr("href") }
                                    else -> ""
                                }

                                if (srcAttr.isNotBlank()) {
                                    contentList.add(srcAttr)
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }

            override fun tail(node: Node, depth: Int) {
                if (node is Element) {
                    when (val tagName = node.tagName().lowercase()) {
                        "b", "strong", "i", "em", "u", "s", "strike", "del", "sup", "sub", "code" -> {
                            if (currentParagraph.isNotEmpty())
                                currentParagraph.append("</$tagName>")
                        }

                        "h1", "h2", "h3", "h4", "h5", "h6" -> {
                            if (currentParagraph.isNotEmpty()) {
                                currentParagraph.append("</$tagName>")
                                if (!insideHeading) {
                                    flushParagraphWithFormatting(currentParagraph, contentList)
                                }
                            }
                            insideHeading = false
                            spaceAddedInHeading = false
                        }

                        "p", "div", "ul", "ol", "li", "table", "blockquote", "hr", "tr",
                        "figure", "figcaption", "details", "summary", "main", "header",
                        "footer", "nav", "aside", "article", "section" -> {
                            if (!insideHeading) {
                                flushParagraphWithFormatting(currentParagraph, contentList)
                            }
                        }

                        "td", "th" -> {
                            if (currentParagraph.isNotEmpty() && !currentParagraph.endsWith(' ')) {
                                currentParagraph.append(" ")
                            }
                        }
                        else -> {}
                    }
                }
            }
        })

        if (!insideHeading) {
            flushParagraphWithFormatting(currentParagraph, contentList)
        }

        val imageRegex = Regex("""(?i)\b[\w./:?-]+?\.(jpg|jpeg|png|gif|webp|bmp|svg)(\?.*)?\b""", RegexOption.IGNORE_CASE)

        val fixedContentList = contentList.mapNotNull {
            val isUrlImage = imageRegex.containsMatchIn(it) || it.startsWith("http")
            val isBase64Image = it.startsWith("data:image/")

            if (isUrlImage || isBase64Image) {
                val filename = "image_${finalBookId}_${chapterIndex}_${contentList.indexOf(it)}"
                val imagePath = downloadAndSaveImage(it, filename)

                if (imagePath.startsWith("error") || imagePath.isEmpty()) {
                    null
                } else {
                    imagePaths.add(imagePath)
                    imagePath
                }
            } else {
                it
            }
        }
        return Pair(fixedContentList, imagePaths)
    }

    private fun flushParagraphWithFormatting(buffer: StringBuilder, list: MutableList<String>) {
        val paragraphText = buffer.toString()
        if (paragraphText.isNotBlank()) {
            list.add(paragraphText)
        }
        buffer.setLength(0)
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ) {
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val importance =
                if (channelId == PROGRESS_CHANNEL_ID) NotificationManager.IMPORTANCE_LOW else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for book import process"
                if (channelId == PROGRESS_CHANNEL_ID) setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createProgressNotificationBuilder(
        fileName: String,
        message: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, PROGRESS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_title_importing_fmt, fileName.take(30) + "..."))
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
        if (progress != null) builder.setProgress(100, progress.coerceIn(0, 100), false)
        else builder.setProgress(0, 0, true)
        try {
            setForeground(getForegroundInfoCompat(builder.build()))
        } catch (_: Exception) {
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun sendCompletionNotification(
        isSuccess: Boolean,
        bookTitle: String?,
        customMessage: String? = null
    ) {
        val title = if (isSuccess) context.getString(R.string.result_success_title) else context.getString(R.string.result_failed_title)
        val defaultTitle = bookTitle ?: context.getString(R.string.error_default_title)

        val text = when {
            customMessage != null -> customMessage
            isSuccess -> context.getString(R.string.result_success_msg_fmt, defaultTitle)
            else -> context.getString(R.string.error_generic_fmt, defaultTitle)
        }

        val builder = NotificationCompat.Builder(context, COMPLETION_CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(text)
            setStyle(NotificationCompat.BigTextStyle().bigText(text))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setAutoCancel(true)
        }
        notificationManager.notify(completionNotificationId, builder.build())
        notificationManager.cancel(notificationId)
    }

    private fun getForegroundInfoCompat(notification: Notification): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }
}