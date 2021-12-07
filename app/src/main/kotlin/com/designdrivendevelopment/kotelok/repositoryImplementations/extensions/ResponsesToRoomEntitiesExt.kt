package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.entities.WordDefinition
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
    return if (synonyms.isNullOrEmpty()) {
        emptyList()
    } else {
        if (synonyms.size > WordDefinition.MAX_TRANSLATIONS_SIZE) {
            synonyms.take(WordDefinition.MAX_TRANSLATIONS_SIZE).map { synonymResponse ->
                SynonymEntity(defId, synonymResponse.writing)
            }
        } else {
            synonyms.map { synonymResponse -> SynonymEntity(defId, synonymResponse.writing) }
        }
    }
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
    return if (otherTranslations.isNullOrEmpty()) {
        emptyList()
    } else {
        if (otherTranslations.size > WordDefinition.MAX_SYNONYMS_SIZE) {
            otherTranslations.take(WordDefinition.MAX_SYNONYMS_SIZE).map { translationResponse ->
                TranslationEntity(defId, translationResponse.writing)
            }
        } else {
            otherTranslations.map { translationResponse ->
                TranslationEntity(defId, translationResponse.writing)
            }
        }
    }
}

fun TranslationResponse.getExampleEntities(defId: Long): List<ExampleEntity> {
    return if (examples.isNullOrEmpty()) {
        emptyList()
    } else {
        if (examples.size > WordDefinition.MAX_EXAMPLES_SIZE) {
            examples.take(WordDefinition.MAX_EXAMPLES_SIZE).map { exampleResponse ->
                ExampleEntity(
                    wordDefinitionId = defId,
                    original = exampleResponse.original,
                    translation = exampleResponse.translations?.first()?.translation
                )
            }
        } else {
            examples.map { exampleResponse ->
                ExampleEntity(
                    wordDefinitionId = defId,
                    original = exampleResponse.original,
                    translation = exampleResponse.translations?.first()?.translation
                )
            }
        }
    }
}
