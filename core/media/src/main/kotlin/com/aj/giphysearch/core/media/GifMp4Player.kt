package com.aj.giphysearch.core.media

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage

@Composable
fun GifMp4Player(
    player: Player?,
    mp4Url: String?,
    stillModel: Any,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    var firstFrameReady by remember(player, mp4Url) { mutableStateOf(false) }
    var fallbackToStill by remember(player, mp4Url) { mutableStateOf(player == null || mp4Url.isNullOrBlank()) }

    DisposableEffect(player, mp4Url) {
        if (player == null || mp4Url.isNullOrBlank()) {
            fallbackToStill = true
            onDispose { }
        } else {
            val listener = object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    firstFrameReady = true
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    fallbackToStill = true
                }
            }
            player.addListener(listener)
            player.setMediaItem(MediaItem.fromUri(mp4Url))
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.volume = 0f
            player.playWhenReady = true
            player.prepare()
            onDispose {
                player.removeListener(listener)
            }
        }
    }

    val surfaceAlpha by animateFloatAsState(
        targetValue = if (!fallbackToStill && firstFrameReady) 1f else 0f,
        label = "mp4-surface-alpha",
    )

    Box(modifier = modifier) {
        AsyncImage(
            model = stillModel,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .testTag("Mp4PlayerUnderlay"),
        )
        if (!fallbackToStill && player != null) {
            AndroidView(
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        useController = false
                        this.player = player
                    }
                },
                update = { playerView ->
                    playerView.player = player
                },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(surfaceAlpha)
                    .testTag("Mp4PlayerSurface"),
            )
        }
    }
}
