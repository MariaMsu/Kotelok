package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DictionaryWordDefinitionsRepository {
    suspend fun getDefinitionsByDictionaryId(dictionaryId: Long): List<WordDefinition>

    suspend fun getAllDefinitions(): List<WordDefinition>

    suspend fun getWordDefinitionById(wordDefinitionId: Long): WordDefinition

    suspend fun getWordDefinitionByWordId(wordId: Long): WordDefinition
}
