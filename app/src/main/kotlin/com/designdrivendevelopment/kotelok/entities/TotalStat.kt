package com.designdrivendevelopment.kotelok.entities

data class TotalStat(
    val totalNumOfWordDefinitions: Int,
    val totalNumOfCompletedTrainings: Int,
    val totalNumOfSuccessfullyTrainings: Int,
    val dictionaryStats: List<DictionaryStat>
)
