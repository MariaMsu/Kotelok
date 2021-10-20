package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Word

interface WordsRepository {
    suspend fun getAllWords(): List<Word>

    suspend fun getWordById(wordId: Long): Word

    suspend fun getWordByWriting(writing: String): Word

    suspend fun addWords(words: List<Word>)

    suspend fun addWord(word: Word)

    suspend fun updateWord(word: Word)

    suspend fun deleteWordById(wordId: Long)

    suspend fun deleteWordsById(wordIds: List<Long>)
}
