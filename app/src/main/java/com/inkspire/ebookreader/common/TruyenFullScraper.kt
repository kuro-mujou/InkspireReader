package com.inkspire.ebookreader.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
    val internalId: String,
    val categories: List<String> = emptyList(),
    val status: String = "Unknown"
)

@Serializable
data class ScrapedChapterRef(
    val title: String,
    val url: String,
    val index: Int
)

@Serializable
data class ScrapedChapterContent(
    val cleanHtml: String
)

object TruyenFullScraper {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    suspend fun search(baseUrl: String, query: String, page: Int = 1): ScrapedPageResult = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim().replace(" ", "+")
        val searchUrl = "$baseUrl/tim-kiem/?tukhoa=$cleanQuery&page=$page"

        val doc = Jsoup.connect(searchUrl).userAgent(USER_AGENT).timeout(30000).get()
        val books = parseBookList(doc)
        val maxPage = parseTotalPages(doc)

        return@withContext ScrapedPageResult(books, maxPage)
    }

    suspend fun fetchCategoryBooks(baseUrl: String, categorySlug: String, page: Int = 1): ScrapedPageResult = withContext(Dispatchers.IO) {
        val categoryUrl = if (page <= 1) {
            "$baseUrl/the-loai/$categorySlug/"
        } else {
            "$baseUrl/the-loai/$categorySlug/trang-$page/"
        }

        val doc = Jsoup.connect(categoryUrl).userAgent(USER_AGENT).timeout(30000).get()
        val books = parseBookList(doc)
        val maxPage = parseTotalPages(doc)

        return@withContext ScrapedPageResult(books, maxPage)
    }

    private fun extractPageNumber(url: String): Int {
        val regex = Regex("(?:trang-|page=)(\\d+)")
        val match = regex.find(url)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    private fun parseTotalPages(doc: Document): Int {
        // Strategy 1: Look for the "Cuối" (Last) link directly.
        // This is the most reliable way to get the true last page.
        val lastLink = doc.select("ul.pagination li a:contains(Cuối)").attr("href")

        if (lastLink.isNotEmpty()) {
            val page = extractPageNumber(lastLink)
            if (page > 0) return page
        }

        // Strategy 2: If "Cuối" is missing (e.g. on page 340 of 343, or short lists),
        // iterate through ALL pagination links and find the highest number.
        val allPageLinks = doc.select("ul.pagination li a")
        var maxPage = 1

        for (link in allPageLinks) {
            val href = link.attr("href")
            val num = extractPageNumber(href)
            if (num > maxPage) maxPage = num
        }

        return maxPage
    }

    /**
     * SHARED PARSER: Used by both Search and Category
     */
    private fun parseBookList(doc: Document): List<ScrapedSearchResult> {
        // Select the rows. Both pages use this structure.
        val bookElements = doc.select(".list-truyen .row")
        val results = mutableListOf<ScrapedSearchResult>()

        for (element in bookElements) {
            try {
                // 1. Title & URL
                val titleElement = element.selectFirst(".truyen-title > a") ?: continue
                val title = titleElement.text()
                val url = titleElement.attr("href")

                // 2. Cover Image (Robust)
                // Priority 1: Check for lazy loading (common on TruyenFull)
                var coverUrl = element.select(".lazyimg").attr("data-image")

                // Priority 2: Check standard src (as seen in your HTML snippet)
                if (coverUrl.isEmpty()) {
                    coverUrl = element.select("img.cover").attr("src")
                }

                // Priority 3: Fallback to any image in the first column
                if (coverUrl.isEmpty()) {
                    coverUrl = element.select(".col-xs-3 img").attr("src")
                }

                // 3. Author
                val author = element.select(".author").text()

                // 4. Latest Chapter
                // Your HTML: <div class="col-xs-2 text-info"> ... <a>Chương 145</a>
                val latestChapter = element.select(".text-info a").text()

                // 5. Status Labels (Full / Hot)
                // These are usually spans with classes label-full or label-hot
                val isFull = element.select(".label-full").isNotEmpty()
                val isHot = element.select(".label-hot").isNotEmpty()

                results.add(
                    ScrapedSearchResult(
                        title = title,
                        author = author,
                        url = url,
                        coverUrl = coverUrl,
                        latestChapter = latestChapter,
                        isFull = isFull,
                        isHot = isHot
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return results
    }

    suspend fun fetchBookDetails(bookUrl: String): ScrapedBookInfo = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(bookUrl).userAgent(USER_AGENT).get()

        val title = doc.select("h3.title").text().ifEmpty { doc.select("h1").text() }
        val author = doc.select(".info a[itemprop='author']").text()
        val descHtml = doc.select(".desc-text").html()
        val coverUrl = doc.select(".book img").attr("src")
        val internalId = doc.select("#truyen-id").attr("value")

        // New fields
        val categories = doc.select(".info a[itemprop='genre']").map { it.text() }
        val status = doc.select(".info span.text-success").text().ifEmpty {
            if (doc.select(".info span.text-primary").isNotEmpty()) "Đang ra" else "Unknown"
        }

        return@withContext ScrapedBookInfo(title, author, descHtml, coverUrl, internalId, categories, status)
    }

    suspend fun fetchTOC(internalId: String, bookUrl: String): List<ScrapedChapterRef> = withContext(Dispatchers.IO) {
        val uri = java.net.URI(bookUrl)
        val domain = "${uri.scheme}://${uri.host}"
        val ajaxUrl = "$domain/ajax.php?type=chapter_option&data=$internalId&bnum=&num=1"

        val doc = Jsoup.connect(ajaxUrl).userAgent(USER_AGENT).get()
        val options = doc.select("option")
        val tocList = mutableListOf<ScrapedChapterRef>()
        val cleanBookUrl = if (bookUrl.endsWith("/")) bookUrl else "$bookUrl/"

        options.forEachIndexed { index, element ->
            val value = element.attr("value")
            val chapterTitle = element.text()
            val fullUrl = if (value.startsWith("http")) value else "$cleanBookUrl$value"
            tocList.add(ScrapedChapterRef(chapterTitle, fullUrl, index))
        }

        return@withContext tocList
    }

    suspend fun fetchChapterContent(chapterUrl: String): String = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(chapterUrl).userAgent(USER_AGENT).get()
        val contentDiv = doc.select(".chapter-c")
        contentDiv.select("script, style, a, iframe, .ads, .ads-holder").remove()
        contentDiv.select("div[style*='font-size:1px'], div[style*='font-size:0px']").remove()
        var html = contentDiv.html()
        html = html.replace(Regex("^\\s*Chương\\s*\\d+\\s?:[^<\\n]+"), "")
        // Optional: Replace nbsp if you want clean text
        // html = html.replace("&nbsp;", " ")
        return@withContext html
    }
}