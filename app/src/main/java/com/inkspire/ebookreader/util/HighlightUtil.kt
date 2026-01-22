package com.inkspire.ebookreader.util

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.inkspire.ebookreader.data.mapper.toInsertModel
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object HighlightUtil {
    fun addHighlight(
        newHighlight: HighlightToInsert,
        currentHighlights: List<Highlight>
    ): List<HighlightToInsert> {
        val paintRangeStart = newHighlight.startOffset
        val paintRangeEnd = newHighlight.endOffset

        val trimmedHighlights = currentHighlights.flatMap { existing ->
            subtractRange(existing, paintRangeStart, paintRangeEnd)
        }

        val result = trimmedHighlights + newHighlight

        return mergeAdjacent(result.sortedBy { it.startOffset })
    }

    fun removeRange(
        rangeStart: Int,
        rangeEnd: Int,
        currentHighlights: List<Highlight>
    ): List<HighlightToInsert> {
        return currentHighlights.flatMap { existing ->
            subtractRange(existing, rangeStart, rangeEnd)
        }.sortedBy { it.startOffset }
    }

    /**
     * Helper: Takes a highlight and removes a specific range from it.
     * Returns a list of 0, 1, or 2 highlights (as `HighlightToInsert` for uniformity).
     */
    private fun subtractRange(
        h: Highlight,
        cutStart: Int,
        cutEnd: Int
    ): List<HighlightToInsert> {
        if (h.endOffset <= cutStart || h.startOffset >= cutEnd) {
            return listOf(h.toInsertModel())
        }

        val result = mutableListOf<HighlightToInsert>()

        if (h.startOffset < cutStart) {
            result.add(
                HighlightToInsert(
                    bookId = "",
                    tocId = 0,
                    paragraphIndex = h.paragraphIndex,
                    startOffset = h.startOffset,
                    endOffset = cutStart,
                    colorIndex = h.colorIndex,
                    note = h.note,
                    createdTime = h.createdTime
                )
            )
        }

        if (h.endOffset > cutEnd) {
            result.add(
                HighlightToInsert(
                    bookId = "",
                    tocId = 0,
                    paragraphIndex = h.paragraphIndex,
                    startOffset = cutEnd,
                    endOffset = h.endOffset,
                    colorIndex = h.colorIndex,
                    note = h.note,
                    createdTime = h.createdTime
                )
            )
        }

        return result
    }

    /**
     * Merges a list of highlights where adjacent items have the same color
     * and their start/end offsets touch.
     * Assumes the list is pre-sorted by `startOffset`.
     */
    private fun mergeAdjacent(list: List<HighlightToInsert>): List<HighlightToInsert> {
        return list.fold(mutableListOf()) { acc, next ->
            val last = acc.lastOrNull()
            if (last != null && last.endOffset == next.startOffset && last.colorIndex == next.colorIndex) {
                acc[acc.size - 1] = last.copy(endOffset = next.endOffset)
            } else {
                acc.add(next)
            }
            acc
        }
    }

    /**
     * Creates a merged selection path with rounded corners.
     * - Snaps right edges to align vertical blocks.
     * - Clamps radius to prevent overlapping curves on small steps.
     */
    fun createRoundedSelectionPath(
        rect: List<Rect>,
        cornerRadius: Float = 16f,
        snapThreshold: Float = 30f
    ): Path {
        if (rect.isEmpty()) return Path()

        val path = Path()

        val snapped = rect.map { it }.toMutableList()

        for (i in 0 until snapped.size - 1) {
            val current = snapped[i]
            val next = snapped[i + 1]

            if (abs(current.right - next.right) < snapThreshold) {
                val maxRight = max(current.right, next.right)
                snapped[i] = current.copy(right = maxRight)
                snapped[i + 1] = next.copy(right = maxRight)
            }
        }

        val first = snapped.first()
        val last = snapped.last()

        path.moveTo(first.left, first.top + cornerRadius)
        path.quadraticTo(first.left, first.top, first.left + cornerRadius, first.top)
        path.lineTo(first.right - cornerRadius, first.top)
        path.quadraticTo(first.right, first.top, first.right, first.top + cornerRadius)

        for (i in 0 until snapped.size - 1) {
            val current = snapped[i]
            val next = snapped[i + 1]

            val boundaryY = current.bottom
            val delta = next.right - current.right
            val effectiveRadius = min(cornerRadius, abs(delta) / 2f)

            if (abs(delta) < 1f) {
                path.lineTo(current.right, next.top)
            } else if (delta > 0) {
                path.lineTo(current.right, boundaryY - effectiveRadius)
                path.quadraticTo(current.right, boundaryY, current.right + effectiveRadius, boundaryY)
                path.lineTo(next.right - effectiveRadius, boundaryY)
                path.quadraticTo(next.right, boundaryY, next.right, boundaryY + effectiveRadius)
            } else {
                path.lineTo(current.right, boundaryY - effectiveRadius)
                path.quadraticTo(current.right, boundaryY, current.right - effectiveRadius, boundaryY)
                path.lineTo(next.right + effectiveRadius, boundaryY)
                path.quadraticTo(next.right, boundaryY, next.right, boundaryY + effectiveRadius)
            }
        }

        path.lineTo(last.right, last.bottom - cornerRadius)
        path.quadraticTo(last.right, last.bottom, last.right - cornerRadius, last.bottom)
        path.lineTo(last.left + cornerRadius, last.bottom)
        path.quadraticTo(last.left, last.bottom, last.left, last.bottom - cornerRadius)

        for (i in snapped.indices.reversed()) {
            if (i == 0) break
            val current = snapped[i]
            val above = snapped[i - 1]

            val boundaryY = above.bottom
            val delta = above.left - current.left
            val effectiveRadius = min(cornerRadius, abs(delta) / 2f)

            if (abs(delta) < 1f) {
                path.lineTo(current.left, boundaryY)
            } else if (delta < 0) {
                path.lineTo(current.left, boundaryY + effectiveRadius)
                path.quadraticTo(current.left, boundaryY, current.left - effectiveRadius, boundaryY)
                path.lineTo(above.left + effectiveRadius, boundaryY)
                path.quadraticTo(above.left, boundaryY, above.left, boundaryY - effectiveRadius)
            } else {
                path.lineTo(current.left, boundaryY + effectiveRadius)
                path.quadraticTo(current.left, boundaryY, current.left + effectiveRadius, boundaryY)
                path.lineTo(above.left - effectiveRadius, boundaryY)
                path.quadraticTo(above.left, boundaryY, above.left, boundaryY - effectiveRadius)
            }
        }

        path.close()
        return path
    }

    /**
     * Recalculates highlights after a portion of the text is replaced.
     * @param rawEditStart The start index in the RAW string.
     * @param rawEditEnd The end index in the RAW string.
     */
    fun recalculateHighlightsAfterEdit(
        highlights: List<Highlight>,
        rawEditStart: Int,
        rawEditEnd: Int,
        newTextLength: Int
    ): List<Highlight> {
        val originalLength = rawEditEnd - rawEditStart
        val lengthDiff = newTextLength - originalLength

        return highlights.mapNotNull { h ->
            if (h.endOffset <= rawEditStart) {
                return@mapNotNull h
            }

            if (h.startOffset >= rawEditEnd) {
                return@mapNotNull h.copy(
                    startOffset = h.startOffset + lengthDiff,
                    endOffset = h.endOffset + lengthDiff
                )
            }

            if (h.startOffset <= rawEditStart && h.endOffset >= rawEditEnd) {
                return@mapNotNull h.copy(
                    endOffset = h.endOffset + lengthDiff
                )
            }

            if (h.startOffset >= rawEditStart && h.endOffset <= rawEditEnd) {
                return@mapNotNull null
            }

            if (h.startOffset < rawEditStart) {
                return@mapNotNull h.copy(endOffset = rawEditStart)
            }

            return@mapNotNull null
        }
    }
}