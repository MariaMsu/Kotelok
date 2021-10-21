package com.designdrivendevelopment.kotelok.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponse(
    @SerialName("text")
    val text: String,
    @SerialName("tr")
    val translations: List<ExampleTranslationResponse>? = null
)
