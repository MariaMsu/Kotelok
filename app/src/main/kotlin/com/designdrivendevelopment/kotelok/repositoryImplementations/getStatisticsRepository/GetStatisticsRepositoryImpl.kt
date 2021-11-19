package com.designdrivendevelopment.kotelok.repositoryImplementations.getStatisticsRepository

import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.TotalStat
import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictStatQueryResult
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toDictionaryStat
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toTotalDictionaryStat
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toWordDefinitionStat
import com.designdrivendevelopment.kotelok.screens.profile.GetStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetStatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : GetStatisticsRepository {
    private var dictStatQueryResultCache = emptyList<DictStatQueryResult>()

    override suspend fun getStatisticsForAllDict(): TotalStat = withContext(Dispatchers.IO) {
        dictStatQueryResultCache = statisticsDao.getStatisticForAllDictionaries()
        val dictStats = dictStatQueryResultCache.map { dictStatQueryResult ->
            dictStatQueryResult.toDictionaryStat()
        }
        return@withContext TotalStat(
            totalNumOfWordDefinitions = dictStatQueryResultCache.sumOf { it.size ?: 0 },
            totalNumOfCompletedTrainings = dictStatQueryResultCache
                .sumOf { it.numOfCompletedTrainings ?: 0 },
            totalNumOfSuccessfullyTrainings = dictStatQueryResultCache
                .sumOf { it.numOfSuccessfullyTrainings ?: 0 },
            dictionaryStats = dictStats
        )
    }

    override suspend fun getStatisticsForDictionary(
        dictionaryId: Long
    ): TotalDictionaryStat = withContext(Dispatchers.IO) {
        val wordDefinitionsStats = statisticsDao.getWordDefinitionsStatsByDictId(dictionaryId)
            .map { wordDefinitionStatQuery ->
                wordDefinitionStatQuery.toWordDefinitionStat()
            }
        if (dictStatQueryResultCache.none { it.id == dictionaryId }) {
            statisticsDao.getTotalStatisticByDictionaryId(dictionaryId)
                .toTotalDictionaryStat(wordDefinitionsStats)
        } else {
            dictStatQueryResultCache.first { it.id == dictionaryId }
                .toTotalDictionaryStat(wordDefinitionsStats)
        }
    }
}
