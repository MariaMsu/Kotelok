package com.designdrivendevelopment.kotelok.trainer.entities

// Вариант перевода
data class Translation(
    val id: Long,
    val language: Language = Language.ENG,
    val description: List<String>,
    val transcription: String,
    val examples: List<String>,
    var learntIndex: Float
)
