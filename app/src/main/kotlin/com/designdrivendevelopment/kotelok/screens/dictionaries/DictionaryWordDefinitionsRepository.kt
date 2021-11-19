package com.designdrivendevelopment.kotelok.screens.dictionaries

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DictionaryWordDefinitionsRepository {
    suspend fun getDefinitionsByDictionaryId(dictionaryId: Long): List<WordDefinition>

    suspend fun getAllDefinitions(): List<WordDefinition>

    suspend fun getWordDefinitionById(wordDefinitionId: Long): WordDefinition
}
