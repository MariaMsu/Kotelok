package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "examples",
    primaryKeys = ["word_def_id", "original_example"],
    foreignKeys = [
        ForeignKey(
            entity = WordDefinitionEntity::class,
            parentColumns = ["def_id"],
            childColumns = ["word_def_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExampleEntity(
    @ColumnInfo(name = "word_def_id")
    val wordDefinitionId: Long,
    @ColumnInfo(name = "original_example")
    val original: String,
    @ColumnInfo(name = "translation")
    val translation: String?
)
