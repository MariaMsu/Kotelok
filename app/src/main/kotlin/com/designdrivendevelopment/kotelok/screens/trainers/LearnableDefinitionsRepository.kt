package com.designdrivendevelopment.kotelok.screens.trainers

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import java.util.Date

interface LearnableDefinitionsRepository {
    suspend fun getAll(): List<LearnableDefinition>

    suspend fun getByDictionaryId(dictionaryId: Long): List<LearnableDefinition>

    suspend fun getByRepeatDate(repeatDate: Date): List<LearnableDefinition>

    suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date
    ): List<LearnableDefinition>

    suspend fun updateLearnableDefinition(wordDefinition: LearnableDefinition)
}
