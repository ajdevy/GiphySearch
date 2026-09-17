package com.aj.giphysearch.core.media

import org.junit.Assert.assertEquals
import org.junit.Test

class VisibleItemsActivationPolicyTest {

    @Test
    fun `activeIndicesFor returns items closest to viewport center`() {
        val layoutInfo = FakeLayoutInfo(
            visibleItems = listOf(
                ActiveItem(index = 0, centerY = 50),
                ActiveItem(index = 1, centerY = 150),
                ActiveItem(index = 2, centerY = 250),
                ActiveItem(index = 3, centerY = 350),
                ActiveItem(index = 4, centerY = 450),
            ),
            viewportStartOffset = 0,
            viewportEndOffset = 500,
        )

        val active = activeIndicesFor(
            visibleItems = layoutInfo.visibleItems,
            viewportStartOffset = layoutInfo.viewportStartOffset,
            viewportEndOffset = layoutInfo.viewportEndOffset,
            maxActiveItems = 3,
        )

        assertEquals(setOf(1, 2, 3), active)
    }

    @Test
    fun `activeIndicesFor returns empty when no visible items`() {
        val layoutInfo = FakeLayoutInfo(
            visibleItems = emptyList(),
            viewportStartOffset = 0,
            viewportEndOffset = 500,
        )

        val active = activeIndicesFor(
            visibleItems = layoutInfo.visibleItems,
            viewportStartOffset = layoutInfo.viewportStartOffset,
            viewportEndOffset = layoutInfo.viewportEndOffset,
            maxActiveItems = 3,
        )

        assertEquals(emptySet<Int>(), active)
    }

    private data class FakeLayoutInfo(
        val visibleItems: List<ActiveItem>,
        val viewportStartOffset: Int,
        val viewportEndOffset: Int,
    )
}
