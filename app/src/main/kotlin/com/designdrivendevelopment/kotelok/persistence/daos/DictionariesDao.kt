package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity

@Dao
interface DictionariesDao {
    @Insert
    suspend fun insert(dictionaryEntity: DictionaryEntity): Long
}
