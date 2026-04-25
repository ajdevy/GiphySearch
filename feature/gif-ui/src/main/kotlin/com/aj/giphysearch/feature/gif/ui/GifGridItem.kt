package com.aj.giphysearch.feature.gif.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aj.giphysearch.core.media.GifMp4Player
import com.aj.giphysearch.core.media.MediaPool
import com.aj.giphysearch.core.media.gridCacheKey
import com.aj.giphysearch.core.media.stillCacheKey
import com.aj.giphysearch.domain.gifs.model.Gif

@Composable
fun GifGridItem(
    gif: Gif,
    isActive: Boolean,
    mediaPool: MediaPool,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val aspectRatio = if (gif.width > 0 && gif.height > 0) {
        gif.width.toFloat() / gif.height.toFloat()
    } else {
        1f
    }
    val itemModifier = modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)
        .clip(RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
        .testTag("GifItem_${gif.id}")

    if (isActive && !gif.gridMp4Url.isNullOrBlank()) {
        val player = mediaPool.acquire(gif.id)
        GifMp4Player(
            player = player,
            mp4Url = gif.gridMp4Url,
            stillModel = ImageRequest.Builder(context)
                .data(gif.images.stillUrl)
                .memoryCacheKey(stillCacheKey(gif.id))
                .build(),
            contentDescription = gif.title,
            modifier = itemModifier,
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(gif.previewUrl)
                .placeholderMemoryCacheKey(stillCacheKey(gif.id))
                .memoryCacheKey(gridCacheKey(gif.id))
                .crossfade(true)
                .build(),
            contentDescription = gif.title,
            contentScale = ContentScale.Crop,
            modifier = itemModifier,
        )
    }
}
