package com.designdrivendevelopment.kotelok.persistence.queryResults

import androidx.room.ColumnInfo

data class DictStatQueryResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "average_skill_level")
    val averageSkillLevel: Float?,
    @ColumnInfo(name = "size")
    val size: Int?,
    @ColumnInfo(name = "completed_num")
    val numOfCompletedTrainings: Int?,
    @ColumnInfo(name = "successfully_num")
    val numOfSuccessfullyTrainings: Int?
)
