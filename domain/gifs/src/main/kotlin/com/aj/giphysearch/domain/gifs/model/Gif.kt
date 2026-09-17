package com.aj.giphysearch.domain.gifs.model

data class Gif(
    val id: String,
    val title: String,
    val rating: String,
    val username: String,
    val source: String,
    val width: Int,
    val height: Int,
    val images: GifImages = GifImages(),
) {
    val originalUrl: String
        get() = images.detailFallbackUrl

    val previewUrl: String
        get() = images.gridPreviewUrl

    val gridMp4Url: String?
        get() = images.gridMp4Url

    val detailMp4Url: String?
        get() = images.detailMp4Url

    constructor(
        id: String,
        title: String,
        rating: String,
        username: String,
        source: String,
        originalUrl: String,
        previewUrl: String,
        width: Int,
        height: Int,
    ) : this(
        id = id,
        title = title,
        rating = rating,
        username = username,
        source = source,
        width = width,
        height = height,
        images = GifImages(
            stillUrl = previewUrl,
            gridPreviewUrl = previewUrl,
            gridMp4Url = null,
            detailMp4Url = null,
            detailFallbackUrl = originalUrl,
        ),
    )
}

data class GifImages(
    val stillUrl: String = "",
    val gridPreviewUrl: String = "",
    val gridMp4Url: String? = null,
    val detailMp4Url: String? = null,
    val detailFallbackUrl: String = "",
)
