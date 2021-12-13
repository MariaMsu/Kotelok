package com.designdrivendevelopment.kotelok.screens.profile

import com.designdrivendevelopment.kotelok.entities.AnswersStatistic
import com.designdrivendevelopment.kotelok.entities.DictionaryStatistic
import kotlinx.coroutines.flow.Flow

interface GetStatisticsRepository {
    fun getStatisticsForAllDict(): Flow<List<DictionaryStatistic>>

    fun getAnswersStatistic(): Flow<AnswersStatistic>
}
