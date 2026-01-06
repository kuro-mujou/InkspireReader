package com.inkspire.ebookreader

import com.inkspire.ebookreader.util.TruyenFullScraper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testTruyenFullScraper() = runBlocking  {
        val toc = TruyenFullScraper.fetchTOC("https://truyenfull.vision/ba-vo-khai-hoang/")
        toc.forEach {
            print("${it.title}\n")
        }
    }
}