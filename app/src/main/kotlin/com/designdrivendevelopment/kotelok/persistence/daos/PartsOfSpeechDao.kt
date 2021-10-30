package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity

@Dao
interface PartsOfSpeechDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(partOfSpeechEntity: PartOfSpeechEntity)
}
