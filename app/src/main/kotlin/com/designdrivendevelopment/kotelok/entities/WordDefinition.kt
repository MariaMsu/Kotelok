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
) {
    companion object {
        const val MAX_TRANSLATIONS_SIZE = 5
        const val MAX_SYNONYMS_SIZE = 5
        const val MAX_EXAMPLES_SIZE = 3
    }
}
