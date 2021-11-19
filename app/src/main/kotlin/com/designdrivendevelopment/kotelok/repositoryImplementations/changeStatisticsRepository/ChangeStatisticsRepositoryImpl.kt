package com.designdrivendevelopment.kotelok.repositoryImplementations.changeStatisticsRepository

import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeStatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : ChangeStatisticsRepository {
    override suspend fun addSuccessfulResultToWordDef(
        wordDefinitionsId: Long
    ) = withContext(Dispatchers.IO) {
        statisticsDao.updateWordDefinitionStat(wordDefinitionsId, SUCCESS)
    }

    override suspend fun addFailedResultToWordDef(
        wordDefinitionsId: Long
    ) = withContext(Dispatchers.IO) {
        statisticsDao.updateWordDefinitionStat(wordDefinitionsId, FAILURE)
    }

    companion object {
        private const val SUCCESS = 1
        private const val FAILURE = 0
    }
}
