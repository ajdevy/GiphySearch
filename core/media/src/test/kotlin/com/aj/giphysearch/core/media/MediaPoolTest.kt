package com.aj.giphysearch.core.media

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger

class MediaPoolTest {

    @Test
    fun `acquire returns same player for same key`() {
        val created = mutableListOf<Player>()
        val pool = MediaPool(capacity = 2) {
            fakePlayer().also(created::add)
        }

        val first = pool.acquire("same-key")
        val second = pool.acquire("same-key")

        assertSame(first, second)
        assertEquals(1, created.size)
    }

    @Test
    fun `acquire evicts oldest when capacity reached`() {
        val pool = MediaPool(capacity = 1) { fakePlayer() }

        pool.acquire("first")
        pool.acquire("second")

        assertEquals(setOf("second"), pool.leasedKeys())
    }

    @Test
    fun `release moves player back to idle queue`() {
        val created = mutableListOf<Player>()
        val pool = MediaPool(capacity = 1) { fakePlayer().also(created::add) }

        val first = pool.acquire("a")
        pool.release("a")
        val second = pool.acquire("b")

        assertSame(first, second)
        assertEquals(1, created.size)
    }

    @Test
    fun `onStop pauses all players`() {
        val pauseCalls = AtomicInteger(0)
        val pool = MediaPool(capacity = 2) { fakePlayer(onPause = { pauseCalls.incrementAndGet() }) }
        pool.acquire("a")
        pool.acquire("b")
        pool.release("b")

        pool.onStop(FakeLifecycleOwner())

        assertEquals(2, pauseCalls.get())
    }

    @Test
    fun `onDestroy releases all players`() {
        val releaseCalls = AtomicInteger(0)
        val pool = MediaPool(capacity = 2) { fakePlayer(onRelease = { releaseCalls.incrementAndGet() }) }
        pool.acquire("a")
        pool.acquire("b")
        pool.release("a")

        pool.onDestroy(FakeLifecycleOwner())

        assertEquals(2, releaseCalls.get())
        assertEquals(0, pool.leasedCount())
    }

    private fun fakePlayer(
        onPause: () -> Unit = {},
        onRelease: () -> Unit = {},
    ): Player {
        val handler = object : InvocationHandler {
            override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? = when (method.name) {
                "pause" -> {
                    onPause()
                    null
                }

                "release" -> {
                    onRelease()
                    null
                }

                "stop", "clearMediaItems" -> null
                "isCommandAvailable" -> false
                "isPlaying" -> false
                "getCurrentPosition", "getDuration", "getBufferedPosition", "getTotalBufferedDuration",
                "getContentPosition", "getContentBufferedPosition", "getCurrentLiveOffset",
                "getSeekBackIncrement", "getSeekForwardIncrement" -> 0L
                "getPlaybackSuppressionReason", "getPlaybackState", "getCurrentMediaItemIndex",
                "getCurrentPeriodIndex", "getCurrentAdGroupIndex", "getCurrentAdIndexInAdGroup",
                "getRepeatMode", "getShuffleModeEnabled", "getRendererCount", "getCurrentTimelineWindowIndex" -> 0
                else -> defaultValue(method.returnType)
            }
        }
        return Proxy.newProxyInstance(
            Player::class.java.classLoader,
            arrayOf(Player::class.java),
            handler,
        ) as Player
    }

    private fun defaultValue(returnType: Class<*>): Any? = when (returnType) {
        java.lang.Boolean.TYPE -> false
        java.lang.Integer.TYPE -> 0
        java.lang.Long.TYPE -> 0L
        java.lang.Float.TYPE -> 0f
        java.lang.Double.TYPE -> 0.0
        else -> null
    }

    private class FakeLifecycleOwner : LifecycleOwner {
        private val ownerLifecycle = object : Lifecycle() {
            override val currentState: State
                get() = State.RESUMED

            override fun addObserver(observer: LifecycleObserver) = Unit

            override fun removeObserver(observer: LifecycleObserver) = Unit
        }

        override val lifecycle: Lifecycle
            get() = ownerLifecycle
    }
}
