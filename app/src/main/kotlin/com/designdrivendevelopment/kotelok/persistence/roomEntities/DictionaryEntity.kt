package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionaries")
data class DictionaryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean
)
