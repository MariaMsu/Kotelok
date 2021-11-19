package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.TranslationResponse

fun TranslationResponse.toWordDefinition(
    writing: String,
    transcription: String?
): WordDefinition {
    return WordDefinition(
        id = 0,
        writing = writing,
        language = Language.ENG,
        partOfSpeech = this.partOfSpeech.toRuPosOrNull(),
        transcription = transcription,
        synonyms = synonyms?.map { synonymResponse -> synonymResponse.writing }.orEmpty(),
        mainTranslation = translation,
        allTranslations = (
            otherTranslations?.map { otherTranslationResponse ->
                otherTranslationResponse.writing
            }.orEmpty() + translation
            ),
        examples = examples?.map { exampleResponse ->
            ExampleOfDefinitionUse(
                originalText = exampleResponse.original,
                translatedText = exampleResponse.translations?.first()?.translation
            )
        }.orEmpty()
    )
}
