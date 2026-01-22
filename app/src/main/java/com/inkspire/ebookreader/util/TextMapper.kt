package com.inkspire.ebookreader.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.inkspire.ebookreader.domain.model.MappedAnnotatedString

object TextMapper {
    fun convertToAnnotatedStringWithMap(paragraph: String): MappedAnnotatedString {
        val displayToRaw = mutableListOf<Int>()

        val annotatedString = buildAnnotatedString {
            val stack = mutableListOf<String>()
            var currentIndex = 0

            while (currentIndex < paragraph.length) {
                when {
                    paragraph.startsWith("<b>", currentIndex) -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        stack.add("b")
                        currentIndex += 3
                    }
                    paragraph.startsWith("</b>", currentIndex) -> {
                        if (stack.lastOrNull() == "b") { pop(); stack.removeAt(stack.lastIndex) }
                        currentIndex += 4
                    }
                    paragraph.startsWith("<strong>", currentIndex) -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        stack.add("strong")
                        currentIndex += 8
                    }
                    paragraph.startsWith("</strong>", currentIndex) -> {
                        if (stack.lastOrNull() == "strong") { pop(); stack.removeAt(stack.lastIndex) }
                        currentIndex += 9
                    }
                    paragraph.startsWith("<i>", currentIndex) -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        stack.add("i")
                        currentIndex += 3
                    }
                    paragraph.startsWith("</i>", currentIndex) -> {
                        if (stack.lastOrNull() == "i") { pop(); stack.removeAt(stack.lastIndex) }
                        currentIndex += 4
                    }
                    paragraph.startsWith("<em>", currentIndex) -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        stack.add("em")
                        currentIndex += 4
                    }
                    paragraph.startsWith("</em>", currentIndex) -> {
                        if (stack.lastOrNull() == "em") { pop(); stack.removeAt(stack.lastIndex) }
                        currentIndex += 5
                    }
                    paragraph.startsWith("<u>", currentIndex) -> {
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        stack.add("u")
                        currentIndex += 3
                    }
                    paragraph.startsWith("</u>", currentIndex) -> {
                        if (stack.lastOrNull() == "u") { pop(); stack.removeAt(stack.lastIndex) }
                        currentIndex += 4
                    }
                    else -> {
                        displayToRaw.add(currentIndex)
                        append(paragraph[currentIndex])
                        currentIndex++
                    }
                }
            }
            displayToRaw.add(paragraph.length)
        }
        return MappedAnnotatedString(annotatedString, displayToRaw)
    }

    fun convertToAnnotatedStrings(paragraph: String): AnnotatedString {
        return buildAnnotatedString {
            val stack = mutableListOf<String>()
            var currentIndex = 0
            while (currentIndex < paragraph.length) {
                when {
                    paragraph.startsWith("<b>", currentIndex) -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        stack.add("b")
                        currentIndex += 3
                    }

                    paragraph.startsWith("</b>", currentIndex) -> {
                        if (stack.lastOrNull() == "b") {
                            pop()
                            stack.removeAt(stack.lastIndex)
                        }
                        currentIndex += 4
                    }

                    paragraph.startsWith("<strong>", currentIndex) -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        stack.add("strong")
                        currentIndex += 8
                    }

                    paragraph.startsWith("</strong>", currentIndex) -> {
                        if (stack.lastOrNull() == "strong") {
                            pop()
                            stack.removeAt(stack.lastIndex)
                        }
                        currentIndex += 9
                    }

                    paragraph.startsWith("<i>", currentIndex) -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        stack.add("i")
                        currentIndex += 3
                    }

                    paragraph.startsWith("</i>", currentIndex) -> {
                        if (stack.lastOrNull() == "i") {
                            pop()
                            stack.removeAt(stack.lastIndex)
                        }
                        currentIndex += 4
                    }

                    paragraph.startsWith("<em>", currentIndex) -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        stack.add("em")
                        currentIndex += 4
                    }

                    paragraph.startsWith("</em>", currentIndex) -> {
                        if (stack.lastOrNull() == "em") {
                            pop()
                            stack.removeAt(stack.lastIndex)
                        }
                        currentIndex += 5
                    }

                    paragraph.startsWith("<u>", currentIndex) -> {
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        stack.add("u")
                        currentIndex += 3
                    }

                    paragraph.startsWith("</u>", currentIndex) -> {
                        if (stack.lastOrNull() == "u") {
                            pop()
                            stack.removeAt(stack.lastIndex)
                        }
                        currentIndex += 4
                    }

                    else -> {
                        append(paragraph[currentIndex])
                        currentIndex++
                    }
                }
            }
        }
    }
}