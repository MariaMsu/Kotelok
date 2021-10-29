package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import java.util.Date

interface LearnableDefinitionsRepository {
    suspend fun getLearnableDefinitionsByDictionaryId(dictionaryId: Long): List<LearnableDefinition>

    suspend fun getAllLearnableDefinitions(): List<LearnableDefinition>

    suspend fun getLearnableDefinitionById(wordId: Long): LearnableDefinition

    suspend fun getLearnableDefinitionsByRepeatDate(repeatDate: Date): LearnableDefinition

    suspend fun getLearnableDefinitionsByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date
    ): List<LearnableDefinition>

    suspend fun updateLearnableDefinition(wordDefinition: LearnableDefinition)
}
