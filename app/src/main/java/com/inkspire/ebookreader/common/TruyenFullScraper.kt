package com.inkspire.ebookreader.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

@Serializable
data class ScrapedBookInfo(
    val title: String,
    val author: String,
    val descriptionHtml: String,
    val coverUrl: String,
    val internalId: String
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
    suspend fun fetchBookDetails(bookUrl: String): ScrapedBookInfo = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(bookUrl).userAgent(Constant.USER_AGENT).get()

        val title = doc.select("h3.title").text().ifEmpty { doc.select("h1").text() }
        val author = doc.select(".info a[itemprop='author']").text()
        val descHtml = doc.select(".desc-text").html()
        val coverUrl = doc.select(".book img").attr("src")
        val internalId = doc.select("#truyen-id").attr("value")

        return@withContext ScrapedBookInfo(title, author, descHtml, coverUrl, internalId)
    }

    suspend fun fetchTOC(internalId: String, bookUrl: String): List<ScrapedChapterRef> =
        withContext(Dispatchers.IO) {

            val uri = java.net.URI(bookUrl)
            val domain = "${uri.scheme}://${uri.host}"

            val ajaxUrl = "$domain/ajax.php?type=chapter_option&data=$internalId&bnum=&num=1"

            val doc = Jsoup.connect(ajaxUrl).userAgent(Constant.USER_AGENT).get()
            val options = doc.select("option")

            val tocList = mutableListOf<ScrapedChapterRef>()

            val cleanBookUrl = if (bookUrl.endsWith("/")) bookUrl else "$bookUrl/"

            options.forEachIndexed { index, element ->
                val value = element.attr("value")
                val chapterTitle = element.text()

                val fullUrl = if (value.startsWith("http")) {
                    value
                } else {
                    "$cleanBookUrl$value"
                }

                tocList.add(ScrapedChapterRef(chapterTitle, fullUrl, index))
            }

            return@withContext tocList
        }

    suspend fun fetchChapterContent(chapterUrl: String): String = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(chapterUrl).userAgent(Constant.USER_AGENT).get()
        val contentDiv = doc.select(".chapter-c")
        contentDiv.select("script, style, a, iframe, .ads, .ads-holder").remove()
        contentDiv.select("div[style*='font-size:1px'], div[style*='font-size:0px']").remove()
        var html = contentDiv.html()
        html = html.replace(Regex("^\\s*Chương\\s*\\d+\\s?:[^<\\n]+"), "")
        html = html.replace("&nbsp;", " ")
        return@withContext html
    }
}