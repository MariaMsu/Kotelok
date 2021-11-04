package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordEntity

@Dao
interface WordsDao {
    @Insert
    suspend fun insert(wordEntity: WordEntity): Long

    @Insert
    suspend fun insertWords(words: List<WordEntity>)

    @Query("DELETE FROM words WHERE (id = :id)")
    suspend fun deleteWordById(id: Long)

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE (id = :id)")
    suspend fun getWordById(id: Long): WordEntity

    @Update
    suspend fun updateWord(wordEntity: WordEntity)

    @Query("SELECT * FROM words WHERE (writing = :writing)")
    suspend fun getWordByWriting(writing: String): WordEntity
}
