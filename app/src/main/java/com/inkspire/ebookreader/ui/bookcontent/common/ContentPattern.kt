package com.inkspire.ebookreader.ui.bookcontent.common

object ContentPattern {
    val linkPattern = Regex("""\.inkspire\.ebookreader/files/[^ ]*""")
    val headerPatten = Regex("""<h([1-6])[^>]*>(.*?)</h([1-6])>""")
    val headerLevel = Regex("""<h([1-6])>.*?</h\1>""")
    val htmlTagPattern = Regex(pattern = """<[^>]+>""")
}