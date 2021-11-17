package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity

@Dao
interface TranslationsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(translationEntity: TranslationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(translationEntities: List<TranslationEntity>)

    @Query(
        """
            DELETE
            FROM translations
            WHERE (word_def_id = :definitionId) AND translation NOT IN (:newTranslations)
        """
    )
    suspend fun deleteRedundantTranslations(definitionId: Long, newTranslations: List<String>)
}
