package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.DictionaryStat
import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.TotalStat
import com.designdrivendevelopment.kotelok.entities.WordDefinitionStat
import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictStatQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionStatQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : StatisticsRepository {
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
