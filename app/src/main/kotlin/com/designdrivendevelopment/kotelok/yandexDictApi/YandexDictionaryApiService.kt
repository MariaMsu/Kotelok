package com.designdrivendevelopment.kotelok.yandexDictApi

import com.designdrivendevelopment.kotelok.yandexDictApi.responses.YandexDictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexDictionaryApiService {
    @GET("lookup")
    suspend fun lookupWord(
        @Query("text") wordWriting: String,
        @Query("lang") directionOfTranslation: String = "en-ru",
    ): YandexDictionaryResponse
}
