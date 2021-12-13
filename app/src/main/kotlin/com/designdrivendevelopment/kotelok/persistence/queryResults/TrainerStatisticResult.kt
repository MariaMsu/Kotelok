package com.designdrivendevelopment.kotelok.persistence.queryResults

import androidx.room.ColumnInfo

data class TrainersStatisticQueryResult(
    @ColumnInfo(name = "completed_num")
    val totalAnswersNum: Int,
    @ColumnInfo(name = "successfully_num")
    val successfullyAnswersNum: Int
)
