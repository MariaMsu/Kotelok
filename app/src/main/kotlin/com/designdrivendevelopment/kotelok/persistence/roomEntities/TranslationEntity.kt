package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "translations",
    primaryKeys = ["word_def_id", "translation"],
    foreignKeys = [
        ForeignKey(
            entity = WordDefinitionEntity::class,
            parentColumns = ["def_id"],
            childColumns = ["word_def_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TranslationEntity(
    @ColumnInfo(name = "word_def_id")
    val wordDefinitionId: Long,
    @ColumnInfo(name = "translation")
    val translation: String
)
