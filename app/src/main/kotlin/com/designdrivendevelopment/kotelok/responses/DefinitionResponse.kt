package com.designdrivendevelopment.kotelok.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefinitionResponse(
    @SerialName("pos")
    val partOfSpeech: String? = null,
    @SerialName("text")
    val writing: String,
    @SerialName("tr")
    val translations: List<TranslationResponse>,
    @SerialName("ts")
    val transcription: String? = null
)
