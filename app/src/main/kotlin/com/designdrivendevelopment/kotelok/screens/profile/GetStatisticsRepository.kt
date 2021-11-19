package com.designdrivendevelopment.kotelok.screens.profile

import com.designdrivendevelopment.kotelok.entities.TotalDictionaryStat
import com.designdrivendevelopment.kotelok.entities.TotalStat

interface GetStatisticsRepository {
    suspend fun getStatisticsForAllDict(): TotalStat

    suspend fun getStatisticsForDictionary(dictionaryId: Long): TotalDictionaryStat
}
