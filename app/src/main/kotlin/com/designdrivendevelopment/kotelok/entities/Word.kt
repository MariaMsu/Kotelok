package com.designdrivendevelopment.kotelok.entities

data class Word(
    val id: Long,
    val language: Language,
    val writing: String,
    val definitions: List<WordDefinition>
)

// data class WordDefinition(
//    val id: Long,
//    val wordId: Long,
//    val writing: String,
//    val partOfSpeech: PartOfSpeech,
//    val transcription: String,
//    val translations: List<Translation>
// )
//
// data class Translation(
//    val id: Long,
//    val mainTranslation: String,
//    val otherTranslations: List<String>,
//    val synonyms: List<String>,
//    val examples: List<Example>,
//    val learntIndex: Float
// )
