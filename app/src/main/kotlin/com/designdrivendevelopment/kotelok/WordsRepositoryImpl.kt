package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Word
import com.designdrivendevelopment.kotelok.persistence.daos.WordsDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordsRepositoryImpl(private val wordsDao: WordsDao) : WordsRepository {
    override suspend fun getAllWords(): List<Word> = withContext(Dispatchers.IO) {
        wordsDao.getAllWords().map { wordEntity -> wordEntity.toWord() }
    }

    override suspend fun getWordById(wordId: Long): Word = withContext(Dispatchers.IO) {
        wordsDao.getWordById(wordId).toWord()
    }
}

fun WordEntity.toWord(): Word {
    return Word(
        id = this.id,
        language = this.language,
        writing = this.writing
    )
}

fun Word.toWordEntity(): WordEntity {
    return WordEntity(
        id = this.id,
        language = this.language,
        writing = this.writing
    )
}
