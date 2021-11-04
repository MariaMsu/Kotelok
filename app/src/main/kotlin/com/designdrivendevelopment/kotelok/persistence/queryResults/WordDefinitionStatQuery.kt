package com.designdrivendevelopment.kotelok.persistence.queryResults

import androidx.room.ColumnInfo

data class WordDefinitionStatQuery(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "writing")
    val writing: String,
    @ColumnInfo(name = "skill_level")
    val skillLevel: Float,
    @ColumnInfo(name = "completed_num")
    val numOfCompletedTrainings: Int,
    @ColumnInfo(name = "successfully_num")
    val numOfSuccessfullyTrainings: Int
)
