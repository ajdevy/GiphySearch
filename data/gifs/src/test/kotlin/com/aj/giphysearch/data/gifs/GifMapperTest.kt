package com.aj.giphysearch.data.gifs

import org.junit.Assert.assertEquals
import org.junit.Test

class GifMapperTest {

    @Test
    fun `toDomain normalizes null and empty dto values`() {
        val domain = GifDto(
            id = "gif_1",
            title = null,
            rating = null,
            username = null,
            source = null,
            images = GifImagesDto(
                original = GifImageDto(
                    url = null,
                    webp = null,
                    mp4 = null,
                    width = "",
                    height = "invalid",
                ),
                fixedWidth = GifImageDto(
                    url = "",
                    webp = null,
                    mp4 = null,
                    width = null,
                    height = null,
                ),
                fixedWidthDownsampled = GifImageDto(
                    url = "",
                    webp = null,
                    mp4 = null,
                    width = null,
                    height = null,
                ),
                fixedWidthStill = GifImageStillDto(url = null),
            ),
        ).toDomain(sdkIntProvider = { 24 })

        assertEquals("gif_1", domain.id)
        assertEquals("", domain.title)
        assertEquals("", domain.rating)
        assertEquals("", domain.username)
        assertEquals("", domain.source)
        assertEquals("", domain.originalUrl)
        assertEquals("", domain.previewUrl)
        assertEquals("", domain.images.stillUrl)
        assertEquals(null, domain.images.gridMp4Url)
        assertEquals(null, domain.images.detailMp4Url)
        assertEquals(480, domain.width)
        assertEquals(270, domain.height)
    }

    @Test
    fun `toDomain preview url falls back across image variants`() {
        val domain = GifDto(
            id = "gif_2",
            images = GifImagesDto(
                original = GifImageDto(url = "original-url", webp = "original-webp"),
                fixedWidth = GifImageDto(url = "", webp = "", mp4 = "fixed-width-mp4"),
                fixedWidthDownsampled = GifImageDto(url = "downsampled-url", webp = "downsampled-webp"),
                fixedWidthStill = GifImageStillDto(url = "still-url"),
            ),
        ).toDomain(sdkIntProvider = { 28 })

        assertEquals("downsampled-webp", domain.previewUrl)
        assertEquals("still-url", domain.images.stillUrl)
        assertEquals("fixed-width-mp4", domain.images.gridMp4Url)
    }

    @Test
    fun `toDomain uses gif fallback on pre-p devices`() {
        val domain = GifDto(
            id = "gif_3",
            images = GifImagesDto(
                original = GifImageDto(
                    url = "original-gif",
                    webp = "original-webp",
                    mp4 = "original-mp4",
                ),
                fixedWidthDownsampled = GifImageDto(
                    url = "downsampled-gif",
                    webp = "downsampled-webp",
                ),
            ),
        ).toDomain(sdkIntProvider = { 24 })

        assertEquals("downsampled-gif", domain.previewUrl)
        assertEquals("original-gif", domain.originalUrl)
        assertEquals("original-mp4", domain.images.detailMp4Url)
    }
}
