package com.aj.giphysearch.data.gifs

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class GifDtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes with missing optional gif fields`() {
        val dto = json.decodeFromString<GifDto>(
            """
            {
              "id": "gif_1"
            }
            """.trimIndent(),
        )

        assertEquals("gif_1", dto.id)
        assertNull(dto.title)
        assertNull(dto.images)
    }

    @Test
    fun `decodes with explicit null optional gif fields`() {
        val dto = json.decodeFromString<GifDto>(
            """
            {
              "id": "gif_1",
              "title": null,
              "images": null
            }
            """.trimIndent(),
        )

        assertEquals("gif_1", dto.id)
        assertNull(dto.title)
        assertNull(dto.images)
    }

    @Test
    fun `fails when required field is missing`() {
        try {
            json.decodeFromString<GifDto>(
                """
                {
                  "title": "missing id"
                }
                """.trimIndent(),
            )
            fail("Expected MissingFieldException when required id is absent")
        } catch (exception: MissingFieldException) {
            assertEquals(true, exception.message.orEmpty().contains("id"))
        }
    }

    @Test
    fun `decodes extended image rendition fields including mp4 and webp`() {
        val dto = json.decodeFromString<GifDto>(
            """
            {
              "id": "gif_2",
              "images": {
                "original": {
                  "url": "https://example.com/original.gif",
                  "webp": "https://example.com/original.webp",
                  "mp4": "https://example.com/original.mp4",
                  "width": "480",
                  "height": "270"
                },
                "fixed_width": {
                  "url": "https://example.com/fixed.gif",
                  "webp": "https://example.com/fixed.webp",
                  "mp4": "https://example.com/fixed.mp4"
                },
                "fixed_width_still": {
                  "url": "https://example.com/still.gif",
                  "width": "200",
                  "height": "200"
                },
                "fixed_width_downsampled": {
                  "url": "https://example.com/downsampled.gif",
                  "webp": "https://example.com/downsampled.webp"
                }
              }
            }
            """.trimIndent(),
        )

        assertEquals("gif_2", dto.id)
        assertEquals("https://example.com/original.mp4", dto.images?.original?.mp4)
        assertEquals("https://example.com/original.webp", dto.images?.original?.webp)
        assertEquals("https://example.com/fixed.mp4", dto.images?.fixedWidth?.mp4)
        assertEquals("https://example.com/still.gif", dto.images?.fixedWidthStill?.url)
        assertEquals("https://example.com/downsampled.webp", dto.images?.fixedWidthDownsampled?.webp)
    }

    @Test
    fun `decodes when optional mp4 and webp fields are missing`() {
        val dto = json.decodeFromString<GifDto>(
            """
            {
              "id": "gif_3",
              "images": {
                "original": {
                  "url": "https://example.com/original.gif"
                },
                "fixed_width_still": {
                  "url": "https://example.com/still.gif"
                }
              }
            }
            """.trimIndent(),
        )

        assertNull(dto.images?.original?.mp4)
        assertNull(dto.images?.original?.webp)
        assertEquals("https://example.com/still.gif", dto.images?.fixedWidthStill?.url)
        assertTrue(dto.images?.fixedWidthDownsampled == null)
    }
}
