package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef

@Dao
interface DictionaryWordDefCrossRefDao {
    @Insert
    suspend fun insert(dictionaryWordDefCrossRef: DictionaryWordDefCrossRef)
}
