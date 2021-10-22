package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designdrivendevelopment.kotelok.entities.Language

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "language")
    val language: Language,
    @ColumnInfo(name = "writing")
    val writing: String
)
