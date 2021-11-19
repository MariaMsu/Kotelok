package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.TranslationResponse
import java.util.Calendar

fun TranslationResponse.getWordDefinitionEntity(
    writing: String,
    transcription: String?
): WordDefinitionEntity {
    return WordDefinitionEntity(
        id = 0,
        writing = writing,
        partOfSpeech = this.partOfSpeech.toRuPosOrNull(),
        transcription = transcription,
        language = Language.ENG,
        mainTranslation = translation,
        cardsNextRepeatDate = with(Calendar.getInstance()) { time },
        cardsRepetitionNumber = 0,
        cardsInterval = 1,
        writerRepeatDate = with(Calendar.getInstance()) { time },
        writerRepetitionNumber = 0,
        writerInterval = 1,
        pairsNextRepeatDate = with(Calendar.getInstance()) { time },
        pairsRepetitionNumber = 0,
        pairsInterval = 1,
        easinessFactor = 2.5F
    )
}

fun TranslationResponse.getSynonymEntities(defId: Long): List<SynonymEntity> {
    return synonyms?.map { synonymResponse ->
        SynonymEntity(defId, synonymResponse.writing)
    }.orEmpty()
}

fun String?.toRuPosOrNull(): String? {
    return when (this) {
        "adverb" -> "нар."
        "verb" -> "гл."
        "adjective" -> "прил."
        "pronoun" -> "мест."
        "noun" -> "сущ."
        "numeral" -> "числ."
        else -> null
    }
}

fun TranslationResponse.getTranslationEntities(defId: Long): List<TranslationEntity> {
    return (
        otherTranslations
            ?.map { otherTranslationResponse ->
                otherTranslationResponse.writing
            }.orEmpty() + translation
        ).map { translation ->
        TranslationEntity(defId, translation)
    }
}

fun TranslationResponse.getExampleEntities(defId: Long): List<ExampleEntity> {
    return examples?.map { exampleResponse ->
        ExampleEntity(
            wordDefinitionId = defId,
            original = exampleResponse.original,
            translation = exampleResponse.translations?.first()?.translation
        )
    }.orEmpty()
}