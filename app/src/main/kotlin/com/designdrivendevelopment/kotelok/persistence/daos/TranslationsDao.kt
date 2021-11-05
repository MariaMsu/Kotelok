package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity

@Dao
interface TranslationsDao {
    @Insert
    suspend fun insert(translationEntity: TranslationEntity)

    @Insert
    suspend fun insert(translationEntities: List<TranslationEntity>)
}
