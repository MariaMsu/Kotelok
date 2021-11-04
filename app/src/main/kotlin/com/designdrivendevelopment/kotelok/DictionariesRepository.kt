package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DictionariesRepository {
    suspend fun getAllDictionaries(): List<Dictionary>

    suspend fun getDictionaryById(dictionaryId: Long): Dictionary

    suspend fun addDictionary(
        dictionary: Dictionary,
        addedWordDefinitions: List<WordDefinition>? = null
    )

    suspend fun updateDictionary(dictionary: Dictionary)

    suspend fun deleteDictionary(dictionary: Dictionary)

    suspend fun deleteWordDefinitionFromDictionary(
        dictionary: Dictionary,
        wordDefinition: WordDefinition
    )
}
