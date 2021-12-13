package com.designdrivendevelopment.kotelok.persistence.queryResults

import androidx.room.ColumnInfo

data class DictionaryStatisticQueryResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "average_skill_level")
    val averageSkillLevel: Float,
    @ColumnInfo(name = "size")
    val size: Int
)

