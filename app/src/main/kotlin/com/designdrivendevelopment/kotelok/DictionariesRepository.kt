package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DictionariesRepository {
    suspend fun getAllDictionaries(): List<Dictionary>

    suspend fun getDictionaryById(dictionaryId: Long): Dictionary

    suspend fun addDictionary(dictionary: Dictionary)

    suspend fun updateTitleBydId(dictionaryName: String, dictionaryId: Long)

    suspend fun deleteDictionaryById(dictionaryId: Long)

    suspend fun addWordDefinitionInDictionary(wordDefinition: WordDefinition, dictionaryId: Long)

    suspend fun deleteWordDefinitionFromDictionary(wordDefinition: WordDefinition, dictionaryId: Long)
}
