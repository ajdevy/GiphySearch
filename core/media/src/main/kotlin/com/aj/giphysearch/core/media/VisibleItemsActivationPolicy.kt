package com.aj.giphysearch.core.media

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import kotlin.math.abs

private const val VIEWPORT_CENTER_DIVISOR = 2

@Composable
fun rememberActiveIndices(
    gridState: LazyStaggeredGridState,
    maxActiveItems: Int = 3,
): State<Set<Int>> = remember(gridState, maxActiveItems) {
    derivedStateOf {
        val layoutInfo = gridState.layoutInfo
        val activeItems = layoutInfo.visibleItemsInfo.map { item ->
            ActiveItem(
                index = item.index,
                centerY = item.offset.y + item.size.height / 2,
            )
        }
        activeIndicesFor(
            visibleItems = activeItems,
            viewportStartOffset = layoutInfo.viewportStartOffset,
            viewportEndOffset = layoutInfo.viewportEndOffset,
            maxActiveItems = maxActiveItems,
        )
    }
}

fun activeIndicesFor(
    visibleItems: List<ActiveItem>,
    viewportStartOffset: Int,
    viewportEndOffset: Int,
    maxActiveItems: Int,
): Set<Int> {
    return if (maxActiveItems <= 0 || visibleItems.isEmpty()) {
        emptySet()
    } else {
        val viewportCenter = (viewportStartOffset + viewportEndOffset) / VIEWPORT_CENTER_DIVISOR
        visibleItems
            .sortedBy { item -> abs(item.centerY - viewportCenter) }
            .take(maxActiveItems)
            .map { it.index }
            .toSet()
    }
}
