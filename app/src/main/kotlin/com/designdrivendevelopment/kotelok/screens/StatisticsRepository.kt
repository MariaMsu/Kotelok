package com.designdrivendevelopment.kotelok.screens

import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.TotalStat

interface StatisticsRepository {
    suspend fun getStatisticsForAllDict(): TotalStat

    suspend fun getStatisticsForDictionary(dictionaryId: Long): TotalDictionaryStat

    suspend fun addSuccessfulResultToWordDef(wordDefinitionsId: Long)

    suspend fun addFailedResultToWordDef(wordDefinitionsId: Long)
}
