package com.designdrivendevelopment.kotelok.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponse(
    @SerialName("text")
    val original: String,
    @SerialName("tr")
    val translations: List<ExampleTranslationResponse>? = null
)
