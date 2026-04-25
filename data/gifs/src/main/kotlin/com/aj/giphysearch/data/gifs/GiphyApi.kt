package com.aj.giphysearch.data.gifs

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

internal interface GiphyApi {

    @GET("gifs/search")
    suspend fun searchGifs(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("bundle") bundle: String = GIPHY_BUNDLE,
        @Query("fields") fields: String = GIPHY_FIELDS,
    ): GiphyListResponseDto

    @GET("gifs/trending")
    suspend fun getTrendingGifs(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("bundle") bundle: String = GIPHY_BUNDLE,
        @Query("fields") fields: String = GIPHY_FIELDS,
    ): GiphyListResponseDto

    @GET("gifs/{id}")
    suspend fun getGifById(
        @Path("id") id: String,
        @Query("bundle") bundle: String = GIPHY_BUNDLE,
        @Query("fields") fields: String = GIPHY_FIELDS,
    ): GiphySingleResponseDto

    private companion object {
        const val GIPHY_BUNDLE = "messaging_non_clips"
        const val GIPHY_FIELDS =
            "id,title,rating,username,source," +
                "images.original,images.fixed_width,images.fixed_width_still," +
                "images.fixed_width_downsampled"
    }
}
