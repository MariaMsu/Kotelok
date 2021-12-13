package com.designdrivendevelopment.kotelok.repositoryImplementations.getStatisticsRepository

import com.designdrivendevelopment.kotelok.entities.AnswersStatistic
import com.designdrivendevelopment.kotelok.entities.DictionaryStatistic
import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toAnswersStatistic
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toDictionaryStatistic
import com.designdrivendevelopment.kotelok.screens.profile.GetStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetStatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao
) : GetStatisticsRepository {
    override fun getStatisticsForAllDict(): Flow<List<DictionaryStatistic>> {
        return statisticsDao.getDictionariesStatistic().map { queryResult ->
            queryResult.map { it.toDictionaryStatistic() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getAnswersStatistic(): Flow<AnswersStatistic> {
        return statisticsDao.getAnswersStatistic().map { it.toAnswersStatistic() }.flowOn(Dispatchers.IO)
    }
}
