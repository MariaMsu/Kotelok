package com.designdrivendevelopment.kotelok.entities

data class WordDefinition(
    val id: Long,
    val wordId: Long,
    val writing: String,
    val partOfSpeech: PartOfSpeech,
    val transcription: String,
    val synonyms: List<String>,
    val mainTranslation: String,
    val otherTranslations: List<String>,
    val examples: List<ExampleOfDefinitionUse>,
    val learntIndex: Float,
)
