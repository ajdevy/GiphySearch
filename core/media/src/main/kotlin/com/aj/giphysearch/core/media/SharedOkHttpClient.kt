package com.aj.giphysearch.core.media

import okhttp3.OkHttpClient

object SharedOkHttpClient {
    fun create(): OkHttpClient = OkHttpClient.Builder().build()
}
