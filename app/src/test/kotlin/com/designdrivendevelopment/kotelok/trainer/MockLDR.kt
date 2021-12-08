package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository
import java.util.Date

class MockLDR : LearnableDefinitionsRepository {
    val data = listOf(
        LearnableDefinition(
            definitionId = 0,
            writing = "dog",
            partOfSpeech = null,
            mainTranslation = "псина",
            otherTranslations = listOf("выслеживать", "кинологический"),
            examples = emptyList<ExampleOfDefinitionUse>(),
            nextRepeatDate = Date(1638434523),
            repetitionNum = 3,
            lastInterval = 2,
            eFactor = LearnableDefinition.EF_INITIAL_VALUE
        ),
        LearnableDefinition(
            definitionId = 0,
            writing = "unsubstantiated",
            partOfSpeech = null,
            mainTranslation = "необоснованный",
            otherTranslations = listOf("бездоказательный"),
            examples = emptyList<ExampleOfDefinitionUse>(),
            nextRepeatDate = Date(1638434641),
            repetitionNum = 3,
            lastInterval = 2,
            eFactor = LearnableDefinition.EF_INITIAL_VALUE
        ),
        LearnableDefinition(
            definitionId = 0,
            writing = "despondency",
            partOfSpeech = null,
            mainTranslation = "уныние",
            otherTranslations = listOf("отчаянье", "подавленность", "упадок духа"),
            examples = emptyList<ExampleOfDefinitionUse>(),
            nextRepeatDate = Date(1638434782),
            repetitionNum = 3,
            lastInterval = 2,
            eFactor = LearnableDefinition.EF_INITIAL_VALUE
        ),

    )

    override suspend fun getAll(): List<LearnableDefinition> {
        return data
    }

    override suspend fun getByDictionaryId(dictionaryId: Long): List<LearnableDefinition> {
        return data
    }

    override suspend fun getByRepeatDate(repeatDate: Date): List<LearnableDefinition> {
        return data
    }

    override suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date,
    ): List<LearnableDefinition> {
        return data
    }

    override suspend fun updateLearnableDefinition(wordDefinition: LearnableDefinition) {
        // we do not use this method for tests
    }
}
