@file:Suppress("TooManyFunctions")
package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.DictionaryStat
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.entities.PartOfSpeech
import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.Word
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.entities.WordDefinitionStat
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictStatQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.LearnableDefQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionStatQuery
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordEntity

fun Dictionary.toDictionaryEntity(): DictionaryEntity {
    return DictionaryEntity(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite
    )
}

fun DictionaryEntity.toDictionary(size: Int): Dictionary {
    return Dictionary(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite,
        size = size
    )
}

fun WordDefinitionQueryResult.toWordDefinition(): WordDefinition {
    return WordDefinition(
        id = this.id,
        wordId = this.wordId,
        writing = this.writing,
        partOfSpeech = this.partOfSpeechEntity?.toPartOfSpeech(),
        transcription = this.transcription,
        synonyms = this.synonyms.map { synonymEntity -> synonymEntity.writing },
        mainTranslation = this.mainTranslation,
        otherTranslations = this.translations.apply { minus(mainTranslation) }
            .map { translationEntity -> translationEntity.translation },
        examples = this.exampleEntities
            .map { exampleEntity -> exampleEntity.toExampleOfDefinitionUse() },
        nextRepeatDate = this.nextRepeatDate
    )
}

fun PartOfSpeechEntity?.toPartOfSpeech(): PartOfSpeech? {
    if (this == null) {
        return null
    }
    return when (this.russianTitle) {
        PartOfSpeech.NOUN_RU_TITLE -> {
            PartOfSpeech.Noun(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.ADJECTIVE_RU_TITLE -> {
            PartOfSpeech.Adjective(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.ADVERB_RU_TITLE -> {
            PartOfSpeech.Adverb(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.NUMERAL_RU_TITLE -> {
            PartOfSpeech.Numeral(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.PRONOUN_RU_TITLE -> {
            PartOfSpeech.Pronoun(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.VERB_RU_TITLE -> {
            PartOfSpeech.Verb(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        else -> {
            PartOfSpeech.Other(
                language = this.language,
                originalTitle = this.originalTitle,
                russianTitle = this.russianTitle
            )
        }
    }
}

fun ExampleEntity.toExampleOfDefinitionUse(): ExampleOfDefinitionUse {
    return ExampleOfDefinitionUse(
        wordDefinitionId = this.wordDefinitionId,
        originalText = this.original,
        translatedText = this.translation.orEmpty()
    )
}

fun LearnableDefQueryResult.toLearnableDef(): LearnableDefinition {
    return LearnableDefinition(
        definitionId = this.id,
        wordId = this.wordId,
        writing = this.writing,
        partOfSpeech = this.partOfSpeechEntity.toPartOfSpeech(),
        mainTranslation = this.mainTranslation,
        otherTranslations = this.translations.map { tr -> tr.translation },
        examples = this.exampleEntities.map { ex -> ex.toExampleOfDefinitionUse() },
        nextRepeatDate = this.nextRepeatDate,
        repetitionNum = this.repetitionNumber,
        lastInterval = this.interval,
        eFactor = this.easinessFactor
    )
}

fun DictStatQueryResult.toDictionaryStat(): DictionaryStat {
    return DictionaryStat(
        id = this.id,
        label = this.label,
        averageSkillLevel = this.averageSkillLevel ?: 0F
    )
}

fun DictStatQueryResult.toTotalDictionaryStat(
    wordDefinitionsStats: List<WordDefinitionStat>
): TotalDictionaryStat {
    return TotalDictionaryStat(
        label = this.label,
        size = this.size ?: 0,
        numOfCompletedTrainings = this.numOfCompletedTrainings ?: 0,
        numOfSuccessfullyTrainings = this.numOfSuccessfullyTrainings ?: 0,
        wordDefinitionStats = wordDefinitionsStats
    )
}

fun WordDefinitionStatQuery.toWordDefinitionStat(): WordDefinitionStat {
    return WordDefinitionStat(
        id = this.id,
        writing = this.writing,
        skillLevel = this.skillLevel,
        numOfCompletedTrainings = this.numOfCompletedTrainings,
        numOfSuccessfullyTrainings = this.numOfSuccessfullyTrainings
    )
}

fun WordEntity.toWord(): Word {
    return Word(
        id = this.id,
        language = this.language,
        writing = this.writing
    )
}

fun Word.toWordEntity(): WordEntity {
    return WordEntity(
        id = this.id,
        language = this.language,
        writing = this.writing
    )
}
