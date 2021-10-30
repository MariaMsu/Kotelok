package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary

interface DictionariesRepository {
    suspend fun getAllDictionaries(): List<Dictionary>

    suspend fun getDictionaryById(dictionaryId: Long): Dictionary

    suspend fun addDictionary(dictionary: Dictionary)

    suspend fun updateDictionaryBydId(dictionary: Dictionary)

    suspend fun deleteDictionaryById(dictionaryId: Long)
}
