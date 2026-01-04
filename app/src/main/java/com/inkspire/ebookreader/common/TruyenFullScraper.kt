package com.inkspire.ebookreader.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

@Serializable
data class ScrapedSearchResult(
    val title: String,
    val author: String,
    val url: String,
    val coverUrl: String,
    val latestChapter: String,
    val isFull: Boolean,
    val isHot: Boolean
)

@Serializable
data class ScrapedPageResult(
    val data: List<ScrapedSearchResult>,
    val totalPages: Int
)

@Serializable
data class ScrapedBookInfo(
    val title: String,
    val author: String,
    val descriptionHtml: String,
    val coverUrl: String,
    val categories: List<String> = emptyList(),
    val status: String = "Unknown"
)

@Serializable
data class ScrapedChapterRef(
    val title: String,
    val url: String,
    val index: Int
)

object TruyenFullScraper {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    suspend fun search(baseUrl: String, query: String, page: Int = 1): ScrapedPageResult = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim().replace(" ", "+")
        val searchUrl = "$baseUrl/tim-kiem/?tukhoa=$cleanQuery&page=$page"
        val doc = Jsoup.connect(searchUrl).userAgent(USER_AGENT).timeout(30000).get()
        val books = parseBookList(doc)
        val maxPage = parseSearchTotalPages(doc)
        return@withContext ScrapedPageResult(books, maxPage)
    }

    suspend fun fetchCategoryBooks(baseUrl: String, categorySlug: String, page: Int = 1): ScrapedPageResult = withContext(Dispatchers.IO) {
        val categoryUrl = if (page <= 1) "$baseUrl/the-loai/$categorySlug/" else "$baseUrl/the-loai/$categorySlug/trang-$page/"
        val doc = Jsoup.connect(categoryUrl).userAgent(USER_AGENT).timeout(30000).get()
        val books = parseBookList(doc)
        val maxPage = parseSearchTotalPages(doc)
        return@withContext ScrapedPageResult(books, maxPage)
    }

    suspend fun fetchBookDetails(bookUrl: String): ScrapedBookInfo = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(bookUrl).userAgent(USER_AGENT).get()
        val title = doc.select("h3.title").text().ifEmpty { doc.select("h1").text().replace("&nbsp;", " ") }
        val author = doc.select(".info a[itemprop='author']").text().replace("&nbsp;", " ")
        val descHtml = doc.select(".desc-text").html().replace("&nbsp;", " ")
        val coverUrl = doc.select(".book img").attr("src")
        val categories = doc.select(".info a[itemprop='genre']").map { it.text().replace("&nbsp;", " ") }
        val status = doc.select(".info span.text-success").text().ifEmpty {
            if (doc.select(".info span.text-primary").isNotEmpty()) "Đang ra" else "Unknown"
        }
        return@withContext ScrapedBookInfo(title, author, descHtml, coverUrl, categories, status)
    }

    suspend fun fetchTOC(bookUrl: String): List<ScrapedChapterRef> = withContext(Dispatchers.IO) {
        val cleanUrl = bookUrl.trimEnd('/')
        val firstPageUrl = "$cleanUrl/trang-1/"

        val firstDoc = Jsoup.connect(firstPageUrl).userAgent(USER_AGENT).timeout(30000).get()
        val totalPages = parseChapterTotalPages(firstDoc)

        val allChapters = mutableListOf<ScrapedChapterRef>()
        allChapters.addAll(parseChaptersFromDoc(firstDoc))

        if (totalPages > 1) {
            val pageChunks = (2..totalPages).chunked(3)

            for (chunk in pageChunks) {
                coroutineScope {
                    val deferredResults = chunk.map { page ->
                        async {
                            try {
                                delay(Random.nextLong(100, 500))

                                val pageUrl = "$cleanUrl/trang-$page/"
                                val doc = Jsoup.connect(pageUrl).userAgent(USER_AGENT).timeout(30000).get()
                                parseChaptersFromDoc(doc)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                emptyList()
                            }
                        }
                    }
                    allChapters.addAll(deferredResults.awaitAll().flatten())
                }

                if (chunk != pageChunks.last()) {
                    delay(Random.nextLong(200, 500))
                }
            }
        }

        return@withContext allChapters.mapIndexed { index, ref ->
            ref.copy(index = index)
        }
    }

    private fun parseChapterTotalPages(doc: Document): Int {
        val pagination = doc.select("ul.pagination")
        if (pagination.isEmpty()) return 1

        val allLinks = pagination.select("li a")

        for (link in allLinks) {
            if (link.text().contains("Cuối", ignoreCase = true)) {
                val page = extractPageNumber(link.attr("href"))
                if (page > 1) return page
            }
        }

        var maxPage = 1
        for (link in allLinks) {
            val text = link.text().trim()
            if (text.matches(Regex("^\\d+$"))) {
                val page = extractPageNumber(link.attr("href"))
                if (page > maxPage) {
                    maxPage = page
                }
            }
        }

        return maxPage
    }

    private fun parseChaptersFromDoc(doc: Document): List<ScrapedChapterRef> {
        val chapters = mutableListOf<ScrapedChapterRef>()
        val links = doc.select(".list-chapter li a")

        links.forEach { element ->
            var title = element.text()
            if (title.isBlank()) {
                val rawTitle = element.attr("title")
                title = if (rawTitle.contains(" - Chương")) {
                    rawTitle.substringAfterLast(" - ")
                } else {
                    rawTitle
                }
            }
            title = title.replace("&nbsp;", " ").trim()
            val url = element.attr("href")
            chapters.add(ScrapedChapterRef(title, url, -1))
        }
        return chapters
    }

    private fun extractPageNumber(url: String): Int {
        val regex = Regex("(?:trang-|page=)(\\d+)")
        val match = regex.find(url)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    private fun parseSearchTotalPages(doc: Document): Int {
        val lastLink = doc.select("ul.pagination li a:contains(Cuối)").attr("href")
        if (lastLink.isNotEmpty()) {
            val page = extractPageNumber(lastLink)
            if (page > 0) return page
        }
        val allPageLinks = doc.select("ul.pagination li a")
        var maxPage = 1
        for (link in allPageLinks) {
            val href = link.attr("href")
            val num = extractPageNumber(href)
            if (num > maxPage) maxPage = num
        }
        return maxPage
    }

    private fun parseBookList(doc: Document): List<ScrapedSearchResult> {
        val bookElements = doc.select(".list-truyen .row")
        val results = mutableListOf<ScrapedSearchResult>()
        for (element in bookElements) {
            try {
                val titleElement = element.selectFirst(".truyen-title > a") ?: continue
                val title = titleElement.text()
                val url = titleElement.attr("href")
                var coverUrl = element.select(".lazyimg").attr("data-image")
                if (coverUrl.isEmpty()) coverUrl = element.select("img.cover").attr("src")
                if (coverUrl.isEmpty()) coverUrl = element.select(".col-xs-3 img").attr("src")
                val author = element.select(".author").text()
                val latestChapter = element.select(".text-info a").text()
                val isFull = element.select(".label-full").isNotEmpty()
                val isHot = element.select(".label-hot").isNotEmpty()

                results.add(ScrapedSearchResult(title, author, url, coverUrl, latestChapter, isFull, isHot))
            } catch (e: Exception) { e.printStackTrace() }
        }
        return results
    }

    suspend fun fetchChapterContent(chapterUrl: String): String = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(chapterUrl).userAgent(USER_AGENT).timeout(30000).get()
        val contentDiv = doc.select(".chapter-c")
        contentDiv.select("script, style, a, iframe, .ads, .ads-holder").remove()
        contentDiv.select("div[style*='font-size:1px'], div[style*='font-size:0px']").remove()
        var html = contentDiv.html()
        html = html.replace(Regex("^\\s*Chương\\s*\\d+\\s?:[^<\\n]+"), "")
        html = html.replace("&nbsp;", " ")
        return@withContext html
    }
}