package com.designdrivendevelopment.kotelok.trainer

interface ChangeStatisticsRepository {
    suspend fun addSuccessfulResultToWordDef(wordDefinitionsId: Long)

    suspend fun addFailedResultToWordDef(wordDefinitionsId: Long)
}
