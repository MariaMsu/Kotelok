package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Word

interface WordsRepository {
    suspend fun getAllWords(): List<Word>

    suspend fun getWordById(wordId: Long): Word
}
