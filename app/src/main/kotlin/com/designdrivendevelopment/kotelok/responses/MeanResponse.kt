package com.designdrivendevelopment.kotelok.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeanResponse(
    @SerialName("text")
    val text: String
)
