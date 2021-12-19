package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.TranslationResponse

fun TranslationResponse.toWordDefinition(
    writing: String,
    transcription: String?
): WordDefinition {
    val reducedTranslations = if (otherTranslations.isNullOrEmpty()) {
        emptyList()
    } else {
        if (otherTranslations.size > WordDefinition.MAX_TRANSLATIONS_SIZE) {
            otherTranslations.take(WordDefinition.MAX_TRANSLATIONS_SIZE).map { it.writing }
        } else {
            otherTranslations.map { it.writing }
        }
    }
    val reducedSynonyms = if (synonyms.isNullOrEmpty()) {
        emptyList()
    } else {
        if (synonyms.size > WordDefinition.MAX_SYNONYMS_SIZE) {
            synonyms.take(WordDefinition.MAX_SYNONYMS_SIZE).map { it.writing }
        } else {
            synonyms.map { it.writing }
        }
    }
    val reducedExamples = if (examples.isNullOrEmpty()) {
        emptyList()
    } else {
        if (examples.size > WordDefinition.MAX_EXAMPLES_SIZE) {
            examples.take(WordDefinition.MAX_EXAMPLES_SIZE).map { exampleResponse ->
                ExampleOfDefinitionUse(
                    originalText = exampleResponse.original,
                    translatedText = exampleResponse.translations?.first()?.translation
                )
            }
        } else {
            examples.map { exampleResponse ->
                ExampleOfDefinitionUse(
                    originalText = exampleResponse.original,
                    translatedText = exampleResponse.translations?.first()?.translation
                )
            }
        }
    }

    return WordDefinition(
        id = 0,
        writing = writing,
        language = Language.ENG,
        partOfSpeech = this.partOfSpeech.toRuPosOrNull(),
        transcription = transcription,
        synonyms = reducedSynonyms,
        mainTranslation = translation,
        otherTranslations = reducedTranslations,
        examples = reducedExamples
    )
}
