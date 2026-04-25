package com.aj.giphysearch.core.navigation

import androidx.lifecycle.Lifecycle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavControllerExtTest {

    @Test
    fun `canNavigateForState returns true for resumed state`() {
        assertTrue(canNavigateForState(Lifecycle.State.RESUMED))
    }

    @Test
    fun `canNavigateForState returns false for started state`() {
        assertFalse(canNavigateForState(Lifecycle.State.STARTED))
    }

    @Test
    fun `canNavigateForState returns false for null state`() {
        assertFalse(canNavigateForState(null))
    }
}
