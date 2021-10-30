package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity

@Dao
interface SynonymsDao {
    @Insert
    suspend fun insert(synonymEntity: SynonymEntity)
}
