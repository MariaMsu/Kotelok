package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "synonyms",
    primaryKeys = ["word_def_id", "writing"],
    foreignKeys = [
        ForeignKey(
            entity = WordDefinitionEntity::class,
            parentColumns = ["def_id"],
            childColumns = ["word_def_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SynonymEntity(
    @ColumnInfo(name = "word_def_id")
    val wordDefinitionId: Long,
    @ColumnInfo(name = "writing")
    val writing: String
)
