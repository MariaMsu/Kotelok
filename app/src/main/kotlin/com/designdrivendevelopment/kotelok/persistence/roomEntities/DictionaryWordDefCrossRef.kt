package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "dictionary_word_def_cross_refs",
    primaryKeys = ["dict_id", "word_def_id"]
)
data class DictionaryWordDefCrossRef(
    @ColumnInfo(name = "dict_id")
    val dictionaryId: Long,
    @ColumnInfo(name = "word_def_id")
    val wordDefinitionId: Long
)
