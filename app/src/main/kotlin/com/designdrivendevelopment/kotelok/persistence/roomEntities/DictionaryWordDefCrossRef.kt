package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "dictionary_word_def_cross_refs",
    primaryKeys = ["dict_id", "word_def_id"],
    foreignKeys = [
        ForeignKey(
            entity = WordDefinitionEntity::class,
            parentColumns = ["def_id"],
            childColumns = ["word_def_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dict_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DictionaryWordDefCrossRef(
    @ColumnInfo(name = "dict_id")
    val dictionaryId: Long,
    @ColumnInfo(name = "word_def_id")
    val wordDefinitionId: Long
)
