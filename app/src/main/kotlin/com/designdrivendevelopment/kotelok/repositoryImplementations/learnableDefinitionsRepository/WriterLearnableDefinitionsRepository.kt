package com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.WriterLearnableDefDao
import com.designdrivendevelopment.kotelok.repositoryImplementations.toLearnableDef
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WriterLearnableDefinitionsRepository(
    private val writerLearnableDefDao: WriterLearnableDefDao
) : LearnableDefinitionsRepository {
    override suspend fun getAll(): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        writerLearnableDefDao.getAllLearnableDefinitions().map { learnableDefQueryResult ->
            learnableDefQueryResult.toLearnableDef()
        }
    }

    override suspend fun getByDictionaryId(
        dictionaryId: Long
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        writerLearnableDefDao
            .getLearnableDefinitionsByDictId(dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByRepeatDate(
        repeatDate: Date
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        writerLearnableDefDao
            .getLearnableDefinitionsByDate(repeatDate.time)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun getByDictionaryIdAndRepeatDate(
        dictionaryId: Long,
        repeatDate: Date,
    ): List<LearnableDefinition> = withContext(Dispatchers.IO) {
        writerLearnableDefDao
            .getLearnableDefinitionsByDateAndDictId(repeatDate.time, dictionaryId)
            .map { learnableDefQueryResult ->
                learnableDefQueryResult.toLearnableDef()
            }
    }

    override suspend fun updateLearnableDefinition(
        wordDefinition: LearnableDefinition
    ) = withContext(Dispatchers.IO) {
        writerLearnableDefDao.updateWordDefinition(
            wordDefinitionId = wordDefinition.definitionId,
            nextRepeatDateInMillis = wordDefinition.repeatDate.time,
            repetitionNumber = wordDefinition.repetitionNumber,
            interval = wordDefinition.interval,
            easinessFactor = wordDefinition.easinessFactor
        )
    }
}
