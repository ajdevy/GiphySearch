package com.aj.giphysearch.core.media

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player

class MediaPool(
    private val capacity: Int = DEFAULT_CAPACITY,
    private val playerFactory: () -> Player,
) : DefaultLifecycleObserver {

    private val idlePlayers = ArrayDeque<Player>()
    private val leasedPlayers = LinkedHashMap<String, Player>()

    @Synchronized
    fun acquire(key: String): Player {
        leasedPlayers[key]?.let { return it }
        val player = idlePlayers.removeFirstOrNull() ?: if (leasedPlayers.size < capacity) {
            playerFactory()
        } else {
            evictLeastRecent()
        }
        leasedPlayers[key] = player
        return player
    }

    @Synchronized
    fun release(key: String) {
        leasedPlayers.remove(key)?.let { player ->
            player.stop()
            player.clearMediaItems()
            idlePlayers.addLast(player)
        }
    }

    @Synchronized
    override fun onStop(owner: LifecycleOwner) {
        leasedPlayers.values.forEach(Player::pause)
        idlePlayers.forEach(Player::pause)
    }

    @Synchronized
    override fun onDestroy(owner: LifecycleOwner) {
        leasedPlayers.values.forEach(Player::release)
        idlePlayers.forEach(Player::release)
        leasedPlayers.clear()
        idlePlayers.clear()
    }

    @VisibleForTesting
    @Synchronized
    fun leasedCount(): Int = leasedPlayers.size

    @VisibleForTesting
    @Synchronized
    fun leasedKeys(): Set<String> = leasedPlayers.keys.toSet()

    @Synchronized
    private fun evictLeastRecent(): Player {
        val oldestKey = leasedPlayers.keys.first()
        return leasedPlayers.remove(oldestKey)!!.also { player ->
            player.stop()
            player.clearMediaItems()
        }
    }

    private companion object {
        const val DEFAULT_CAPACITY = 3
    }
}
