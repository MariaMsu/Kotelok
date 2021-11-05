package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LearnableDefinitionsRepositoryImpl(
    private val wordDefinitionsDao: WordDefinitionsDao
) : LearnableDefinitionsRepository {
    override suspend fun getAll(): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getAllLearnableDefinitions().map { learnableDefQueryResult ->
            learnableDefQueryResult.toLearnableDef()
        }
    }

    override suspend fun getByDictionaryId(
        dictionaryId: Long
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao
            .getLearnableDefinitionsByDictId(dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByRepeatDate(
        repeatDate: Date
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao
            .getLearnableDefinitionsByDate(repeatDate.time)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date,
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao
            .getLearnableDefinitionsByDateAndDictId(repeatDate.time, dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun updateLearnableDefinition(
        wordDefinition: LearnableDefinition
    ) = withContext(Dispatchers.IO) {
        wordDefinitionsDao.updateWordDefinition(
            wordDefinitionId = wordDefinition.definitionId,
            nextRepeatDateInMillis = wordDefinition.repeatDate.time,
            repetitionNumber = wordDefinition.repetitionNumber,
            interval = wordDefinition.interval,
            easinessFactor = wordDefinition.easinessFactor
        )
    }
}
