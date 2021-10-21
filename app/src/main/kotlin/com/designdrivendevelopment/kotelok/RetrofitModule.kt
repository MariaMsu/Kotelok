package com.designdrivendevelopment.kotelok

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.create

class RetrofitModule {
    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(AuthInterceptor())
        .build()
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dictionary.yandex.net/api/v1/dicservice.json/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val yandexDictionaryService = retrofit.create<YandexDictionaryApiService>()

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val originalRequestUrl = originalRequest.url
            val newUrl = originalRequestUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.YANDEX_DICT_API_KEY)
                .build()
            val changedRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(changedRequest)
        }
    }
}
