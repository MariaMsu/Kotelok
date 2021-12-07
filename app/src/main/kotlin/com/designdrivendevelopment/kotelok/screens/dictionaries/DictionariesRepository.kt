package com.designdrivendevelopment.kotelok.screens.dictionaries

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import kotlinx.coroutines.flow.Flow

interface DictionariesRepository {
    suspend fun getAllDictionaries(): List<Dictionary>

    fun getAllDictionariesFlow(): Flow<List<Dictionary>>

    suspend fun getDictionaryById(dictionaryId: Long): Dictionary

    suspend fun addDictionary(
        dictionary: Dictionary,
        addedWordDefinitions: List<WordDefinition>? = null
    )

    suspend fun updateDictionary(dictionary: Dictionary)

    suspend fun updateDictionaries(dictionaries: List<Dictionary>)

    suspend fun deleteDictionary(dictionary: Dictionary)

    suspend fun deleteWordDefinitionFromDictionary(
        dictionary: Dictionary,
        wordDefinition: WordDefinition
    )
}
