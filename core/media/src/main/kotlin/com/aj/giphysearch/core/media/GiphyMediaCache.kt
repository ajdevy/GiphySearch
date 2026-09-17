package com.aj.giphysearch.core.media

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.OkHttpClient
import java.io.File

@UnstableApi
class GiphyMediaCache(
    context: Context,
    okHttpClient: OkHttpClient,
    maxBytes: Long = DEFAULT_MAX_CACHE_BYTES,
) {
    private val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
    private val simpleCache = SimpleCache(
        File(context.cacheDir, CACHE_DIR_NAME),
        LeastRecentlyUsedCacheEvictor(maxBytes),
        databaseProvider,
    )
    private val upstreamDataSourceFactory: DataSource.Factory = OkHttpDataSource.Factory(okHttpClient)

    fun dataSourceFactory(): DataSource.Factory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    companion object {
        private const val CACHE_DIR_NAME = "giphy_media"
        private const val DEFAULT_MAX_CACHE_BYTES = 64L * 1024L * 1024L
    }
}
