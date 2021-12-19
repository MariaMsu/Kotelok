package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.AnswersStatistic
import com.designdrivendevelopment.kotelok.entities.DictionaryStatistic
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.queryResults.AnswersStatisticQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictionaryStatisticQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.LearnableDefQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult

fun WordDefinitionQueryResult.toWordDefinition(): WordDefinition {
    return WordDefinition(
        id = this.id,
        writing = this.writing,
        partOfSpeech = this.partOfSpeech,
        transcription = this.transcription,
        synonyms = this.synonyms.map { synonymEntity -> synonymEntity.writing },
        mainTranslation = this.mainTranslation,
        otherTranslations = this.translations
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

fun DictionaryStatisticQueryResult.toDictionaryStatistic(): DictionaryStatistic {
    return DictionaryStatistic(
        id = this.id,
        label = this.label,
        size = this.size,
        averageSkillLevel = this.averageSkillLevel
    )
}

fun AnswersStatisticQueryResult.toAnswersStatistic(): AnswersStatistic {
    return AnswersStatistic(
        totalAnswers = totalAnswersNum,
        successfullyAnswers = successfullyAnswersNum
    )
}
