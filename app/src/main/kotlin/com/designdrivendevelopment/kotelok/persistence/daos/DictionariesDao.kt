package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionariesDao {
    @Insert
    suspend fun insert(dictionaryEntity: DictionaryEntity): Long

    @Update
    suspend fun update(dictionaryEntity: DictionaryEntity)

    @Update
    suspend fun update(dictionaryEntities: List<DictionaryEntity>)

    @Query("DELETE FROM dictionaries WHERE (id = :dictionaryId)")
    suspend fun deleteById(dictionaryId: Long)

    @Query("SELECT * FROM dictionaries")
    suspend fun getAll(): List<DictionaryEntity>

    @Query("SELECT * FROM dictionaries")
    fun getAllFlow(): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionaries WHERE (id = :dictionaryId)")
    suspend fun getDictionaryById(dictionaryId: Long): DictionaryEntity

    @Query(
        """
        SELECT COUNT(*)
        FROM dictionary_word_def_cross_refs
        WHERE (dict_id = :dictionaryId)
        """
    )
    suspend fun getDictionarySizeById(dictionaryId: Long): Int
}
