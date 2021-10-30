package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordEntity

@Dao
interface WordsDao {
    @Insert
    suspend fun insert(wordEntity: WordEntity): Long

    @Query("SELECT * FROM words WHERE (writing = :writing)")
    suspend fun getWordByWriting(writing: String): WordEntity
}
