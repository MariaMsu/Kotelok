package com.designdrivendevelopment.kotelok.yandexDictApi.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleTranslationResponse(
    @SerialName("text")
    val translation: String
)
