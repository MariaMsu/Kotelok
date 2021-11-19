package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.DictionaryStat
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.entities.WordDefinitionStat
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictStatQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.LearnableDefQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionStatQuery

fun WordDefinitionQueryResult.toWordDefinition(): WordDefinition {
    return WordDefinition(
        id = this.id,
        writing = this.writing,
        partOfSpeech = this.partOfSpeech,
        transcription = this.transcription,
        synonyms = this.synonyms.map { synonymEntity -> synonymEntity.writing },
        mainTranslation = this.mainTranslation,
        allTranslations = this.translations
            .map { translationEntity -> translationEntity.translation },
        examples = this.exampleEntities
            .map { exampleEntity -> exampleEntity.toExampleOfDefinitionUse() }
    )
}

fun LearnableDefQueryResult.toLearnableDef(): LearnableDefinition {
    return LearnableDefinition(
        definitionId = this.id,
        writing = this.writing,
        partOfSpeech = this.partOfSpeech,
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
