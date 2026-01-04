package com.inkspire.ebookreader.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URI

object TangThuVienScraper {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    /**
     * STEP 1: Fetch Book Info
     */
    suspend fun fetchBookDetails(bookUrl: String): ScrapedBookInfo = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(bookUrl).userAgent(USER_AGENT).get()

        val title = doc.select("h1").text()
        val author = doc.select("#authorId").text()
        val descHtml = doc.select(".book-intro").html()

        // FIX COVER: Try multiple selectors because mirrors differ
        // 1. #bookImg (Old ID)
        // 2. .book-img img (Standard Class)
        var coverUrl = doc.select("#bookImg").attr("src")
        if (coverUrl.isEmpty()) {
            coverUrl = doc.select(".book-img img").attr("src")
        }

        // Fix relative URLs (//static...)
        if (coverUrl.startsWith("//")) {
            coverUrl = "https:$coverUrl"
        }

        return@withContext ScrapedBookInfo(title, author, descHtml, coverUrl)
    }

    /**
     * STEP 2: Fetch TOC
     * UPDATED: Accepts [bookUrl] to determine the correct API domain
     */
    suspend fun fetchTOC(internalId: String, bookUrl: String): List<ScrapedChapterRef> = withContext(Dispatchers.IO) {
        // 1. Extract Domain (e.g., https://tangthuvien.net)
        val uri = URI(bookUrl)
        val baseUrl = "${uri.scheme}://${uri.host}"

        // 2. Construct Dynamic API URL
        // Endpoint: /story/chapters?story_id=33049
        val apiUrl = "$baseUrl/story/chapters?story_id=$internalId"

        val doc = Jsoup.connect(apiUrl)
            .userAgent(USER_AGENT)
            .header("X-Requested-With", "XMLHttpRequest") // Required for AJAX
            .get()

        val links = doc.select("a")
        val tocList = mutableListOf<ScrapedChapterRef>()

        links.forEachIndexed { index, element ->
            val title = element.text()
            val href = element.attr("href")

            // href is usually: "https://tangthuvien.net/doc-truyen/..."
            // If it's valid, add it.
            if (href.contains("doc-truyen")) {
                tocList.add(ScrapedChapterRef(title, href, index))
            }
        }

        return@withContext tocList
    }

    /**
     * STEP 3: Fetch Content
     */
    suspend fun fetchChapterContent(chapterUrl: String): String = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(chapterUrl).userAgent(USER_AGENT).get()

        // 1. Select the box (CSS Selector is safer)
        val contentDiv = doc.select(".box-chap:not(.hidden)").first()
            ?: throw Exception("Could not find chapter content box")

        // 2. Safer Cleaning
        // REMOVED 'div' from this list to prevent deleting story parts
        contentDiv.select("script, style, iframe, .ads, .ads-holder").remove()

        // Remove internal ad links hidden in paragraphs
        contentDiv.select("p:has(a[href*='tangthuvien'])").remove()

        // 3. Check for VIP/Login Buttons
        if (contentDiv.select("#btnChapterVip").isNotEmpty() ||
            contentDiv.select(".btn-vip-read").isNotEmpty()) {
            return@withContext "<p><em>This chapter is VIP. Please login via WebView to read.</em></p>"
        }

        // 4. Check for "Hidden" Text (Blur effect)
        // TangThuVien sometimes puts the 2nd half of text in a div with 'opacity: 0.1' or similar for guests
        val hiddenText = contentDiv.select(".truyen-text-hidden")
        if (hiddenText.isNotEmpty()) {
            // We try to unhide it, but often the server doesn't send the full text to guests.
            // You might need the WebView method for this specific book if this happens.
        }

        // 5. Fix Images
        contentDiv.select("img").forEach { img ->
            var src = img.attr("src")
            if (src.startsWith("//")) src = "https:$src"
            img.attr("src", src)
        }

        return@withContext contentDiv.html()
    }
}