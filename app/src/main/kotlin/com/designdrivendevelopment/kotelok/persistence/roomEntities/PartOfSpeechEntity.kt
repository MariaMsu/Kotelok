package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designdrivendevelopment.kotelok.entities.Language

@Entity(tableName = "parts_of_speech")
data class PartOfSpeechEntity(
    @ColumnInfo(name = "language")
    val language: Language,
    @PrimaryKey
    @ColumnInfo(name = "original_title")
    val originalTitle: String,
    @ColumnInfo(name = "ru_title")
    val russianTitle: String,
)
