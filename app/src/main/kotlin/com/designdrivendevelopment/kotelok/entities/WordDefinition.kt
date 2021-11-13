package com.designdrivendevelopment.kotelok.entities

data class WordDefinition(
    val id: Long,
    val writing: String,
    val language: Language = Language.ENG,
    val partOfSpeech: String?,
    val transcription: String?,
    val synonyms: List<String>,
    val mainTranslation: String,
    val allTranslations: List<String>,
    val examples: List<ExampleOfDefinitionUse>
)
