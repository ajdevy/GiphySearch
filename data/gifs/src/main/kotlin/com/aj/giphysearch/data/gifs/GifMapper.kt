package com.aj.giphysearch.data.gifs

import android.os.Build
import com.aj.giphysearch.domain.gifs.model.Gif
import com.aj.giphysearch.domain.gifs.model.GifImages

private const val DEFAULT_GIF_WIDTH = 480
private const val DEFAULT_GIF_HEIGHT = 270

internal fun GifDto.toDomain(
    sdkIntProvider: () -> Int = { Build.VERSION.SDK_INT },
): Gif {
    val sdkInt = sdkIntProvider()
    val original = images?.original
    val fixedWidth = images?.fixedWidth
    val fixedWidthDownsampled = images?.fixedWidthDownsampled
    val fixedWidthStill = images?.fixedWidthStill

    val detailFallbackUrl = selectAnimatedUrl(
        webp = original?.webp,
        gif = original?.url,
        sdkInt = sdkInt,
    )
    val gridPreviewUrl = selectAnimatedUrl(
        webp = fixedWidthDownsampled?.webp ?: fixedWidth?.webp ?: original?.webp,
        gif = fixedWidthDownsampled?.url ?: fixedWidth?.url ?: original?.url,
        sdkInt = sdkInt,
    )

    return Gif(
        id = id,
        title = title.orEmpty(),
        rating = rating.orEmpty(),
        username = username.orEmpty(),
        source = source.orEmpty(),
        width = original?.width?.toIntOrNull() ?: DEFAULT_GIF_WIDTH,
        height = original?.height?.toIntOrNull() ?: DEFAULT_GIF_HEIGHT,
        images = GifImages(
            stillUrl = fixedWidthStill?.url.orEmpty().ifEmpty { gridPreviewUrl },
            gridPreviewUrl = gridPreviewUrl,
            gridMp4Url = fixedWidth?.mp4,
            detailMp4Url = original?.mp4,
            detailFallbackUrl = detailFallbackUrl,
        ),
    )
}

private fun selectAnimatedUrl(webp: String?, gif: String?, sdkInt: Int): String {
    if (sdkInt >= Build.VERSION_CODES.P) {
        return webp.orEmpty().ifEmpty { gif.orEmpty() }
    }
    return gif.orEmpty().ifEmpty { webp.orEmpty() }
}
