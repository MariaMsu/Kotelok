package com.designdrivendevelopment.kotelok.entities

data class TotalDictionaryStat(
    val label: String,
    val size: Int,
    val numOfCompletedTrainings: Int,
    val numOfSuccessfullyTrainings: Int,
    val wordDefinitionStats: List<WordDefinitionStat>
)
