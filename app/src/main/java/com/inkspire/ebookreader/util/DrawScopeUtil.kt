package com.inkspire.ebookreader.util

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun DrawScope.drawRoundedBackground(
    layoutResult: TextLayoutResult,
    startOffset: Int,
    endOffset: Int,
    color: Color,
    padding: Float = 4.dp.toPx(),
    defaultRadius: Float = 8.dp.toPx()
) {
    if (startOffset >= endOffset) return

    val startLine = layoutResult.getLineForOffset(startOffset)
    val endLine = layoutResult.getLineForOffset(endOffset)

    val lineRects = mutableListOf<Rect>()

    // 1. Calculate Rects
    for (lineIndex in startLine..endLine) {
        val lineStart = if (lineIndex == startLine) startOffset else layoutResult.getLineStart(lineIndex)
        val lineEnd = if (lineIndex == endLine) endOffset else layoutResult.getLineEnd(lineIndex, visibleEnd = true)

        if (lineStart >= lineEnd && lineIndex != startLine) continue

        val startX = layoutResult.getHorizontalPosition(lineStart, true)
        val endX = layoutResult.getHorizontalPosition(lineEnd, true)

        val left = min(startX, endX)
        val right = max(startX, endX)
        val top = layoutResult.getLineTop(lineIndex)
        val bottom = layoutResult.getLineBottom(lineIndex)

        lineRects.add(Rect(left - padding, top, right + padding, bottom))
    }

    if (lineRects.isEmpty()) return

    // Limit radius to half line height to prevent self-intersection on small fonts
    val lineHeight = lineRects.first().height
    val radius = defaultRadius.coerceAtMost(lineHeight / 2f)

    val highlightPath = Path()

    // Single Line Case: Simple Rounded Rect
    if (lineRects.size == 1) {
        highlightPath.addRoundRect(
            RoundRect(lineRects.first(), CornerRadius(radius, radius))
        )
    } else {
        // Multi-Line Blob Logic
        val firstRect = lineRects.first()

        // --- TOP ---
        highlightPath.moveTo(firstRect.left, firstRect.top + radius)
        highlightPath.quadraticBezierTo(firstRect.left, firstRect.top, firstRect.left + radius, firstRect.top)
        highlightPath.lineTo(firstRect.right - radius, firstRect.top)
        highlightPath.quadraticBezierTo(firstRect.right, firstRect.top, firstRect.right, firstRect.top + radius)

        // --- RIGHT SIDE (Down) ---
        var prevRect = firstRect
        for (i in 1 until lineRects.size) {
            val currRect = lineRects[i]

            if (abs(currRect.right - prevRect.right) > radius) {
                // If Target is wider (>), Curve Out (+). If Target is narrower (<), Curve In (-).
                val normalizedRadius = if (currRect.right > prevRect.right) radius else -radius

                highlightPath.quadraticBezierTo(
                    prevRect.right, prevRect.bottom,
                    prevRect.right + normalizedRadius, currRect.top
                )
                highlightPath.lineTo(currRect.right - normalizedRadius, currRect.top)
                highlightPath.quadraticBezierTo(
                    currRect.right, currRect.top,
                    currRect.right, currRect.top + radius
                )
            } else {
                // Small difference: Smooth Cubic connector
                highlightPath.cubicTo(
                    prevRect.right, prevRect.bottom,
                    currRect.right, currRect.top,
                    currRect.right, currRect.top + radius
                )
            }
            prevRect = currRect
        }

        // --- BOTTOM ---
        val lastRect = lineRects.last()
        highlightPath.lineTo(lastRect.right, lastRect.bottom - radius)
        highlightPath.quadraticBezierTo(lastRect.right, lastRect.bottom, lastRect.right - radius, lastRect.bottom)
        highlightPath.lineTo(lastRect.left + radius, lastRect.bottom)
        highlightPath.quadraticBezierTo(lastRect.left, lastRect.bottom, lastRect.left, lastRect.bottom - radius)

        // --- LEFT SIDE (Up) ---
        // We iterate backwards from the 2nd-to-last line up to the first
        prevRect = lastRect // Start with the bottom line
        for (i in lineRects.size - 2 downTo 0) {
            val currRect = lineRects[i] // The line ABOVE

            if (abs(currRect.left - prevRect.left) > radius) {
                // Logic Correction:
                // We are at Prev(Bottom).Left. We want to go to Curr(Top).Left.
                // If Curr is further RIGHT (>), we curve IN (Positive Radius).
                // If Curr is further LEFT (<), we curve OUT (Negative Radius).
                val normalizedRadius = if (currRect.left > prevRect.left) radius else -radius

                highlightPath.quadraticBezierTo(
                    prevRect.left, prevRect.top, // Control point is corner of Bottom line
                    prevRect.left + normalizedRadius, currRect.bottom // End point
                )
                highlightPath.lineTo(currRect.left - normalizedRadius, currRect.bottom)
                highlightPath.quadraticBezierTo(
                    currRect.left, currRect.bottom,
                    currRect.left, currRect.bottom - radius
                )
            } else {
                highlightPath.cubicTo(
                    prevRect.left, prevRect.top,
                    currRect.left, currRect.bottom,
                    currRect.left, currRect.bottom - radius
                )
            }
            prevRect = currRect
        }

        highlightPath.close()
    }

    drawPath(path = highlightPath, color = color)
}