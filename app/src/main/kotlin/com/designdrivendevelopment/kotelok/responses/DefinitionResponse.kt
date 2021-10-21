package com.designdrivendevelopment.kotelok.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefinitionResponse(
    @SerialName("pos")
    val partOfSpeech: String,
    @SerialName("text")
    val text: String,
    @SerialName("tr")
    val translations: List<TranslationResponse>,
    @SerialName("ts")
    val transcription: String
)
