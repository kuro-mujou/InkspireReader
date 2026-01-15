package com.inkspire.ebookreader.util

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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

    // 1. Pre-process: Snap Right Sides
    val snapped = rect.map { it }.toMutableList()

    for (i in 0 until snapped.size - 1) {
        val current = snapped[i]
        val next = snapped[i + 1]

        // If right edges are close, snap to the widest one
        if (abs(current.right - next.right) < snapThreshold) {
            val maxRight = max(current.right, next.right)
            snapped[i] = current.copy(right = maxRight)
            snapped[i + 1] = next.copy(right = maxRight)
        }
    }

    // 2. Draw Path (Clockwise)
    val first = snapped.first()
    val last = snapped.last()

    // Top
    path.moveTo(first.left, first.top + cornerRadius)
    path.quadraticTo(first.left, first.top, first.left + cornerRadius, first.top)
    path.lineTo(first.right - cornerRadius, first.top)
    path.quadraticTo(first.right, first.top, first.right, first.top + cornerRadius)

    // Right Side (Down)
    for (i in 0 until snapped.size - 1) {
        val current = snapped[i]
        val next = snapped[i + 1]

        val boundaryY = current.bottom
        val delta = next.right - current.right
        val effectiveRadius = min(cornerRadius, abs(delta) / 2f)

        if (abs(delta) < 1f) {
            path.lineTo(current.right, next.top) // Vertical Merge
        } else if (delta > 0) {
            // Step Out (Concave)
            path.lineTo(current.right, boundaryY - effectiveRadius)
            path.quadraticTo(current.right, boundaryY, current.right + effectiveRadius, boundaryY)
            path.lineTo(next.right - effectiveRadius, boundaryY)
            path.quadraticTo(next.right, boundaryY, next.right, boundaryY + effectiveRadius)
        } else {
            // Step In (Convex)
            path.lineTo(current.right, boundaryY - effectiveRadius)
            path.quadraticTo(current.right, boundaryY, current.right - effectiveRadius, boundaryY)
            path.lineTo(next.right + effectiveRadius, boundaryY)
            path.quadraticTo(next.right, boundaryY, next.right, boundaryY + effectiveRadius)
        }
    }

    // Bottom
    path.lineTo(last.right, last.bottom - cornerRadius)
    path.quadraticTo(last.right, last.bottom, last.right - cornerRadius, last.bottom)
    path.lineTo(last.left + cornerRadius, last.bottom)
    path.quadraticTo(last.left, last.bottom, last.left, last.bottom - cornerRadius)

    // Left Side (Up)
    for (i in snapped.indices.reversed()) {
        if (i == 0) break
        val current = snapped[i]
        val above = snapped[i - 1]

        val boundaryY = above.bottom
        val delta = above.left - current.left
        val effectiveRadius = min(cornerRadius, abs(delta) / 2f)

        if (abs(delta) < 1f) {
            path.lineTo(current.left, boundaryY) // Vertical Merge
        } else if (delta < 0) {
            // Step Out (Concave)
            path.lineTo(current.left, boundaryY + effectiveRadius)
            path.quadraticTo(current.left, boundaryY, current.left - effectiveRadius, boundaryY)
            path.lineTo(above.left + effectiveRadius, boundaryY)
            path.quadraticTo(above.left, boundaryY, above.left, boundaryY - effectiveRadius)
        } else {
            // Step In (Convex)
            path.lineTo(current.left, boundaryY + effectiveRadius)
            path.quadraticTo(current.left, boundaryY, current.left + effectiveRadius, boundaryY)
            path.lineTo(above.left - effectiveRadius, boundaryY)
            path.quadraticTo(above.left, boundaryY, above.left, boundaryY - effectiveRadius)
        }
    }

    path.close()
    return path
}