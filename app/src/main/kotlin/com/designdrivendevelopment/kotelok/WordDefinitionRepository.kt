package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface WordDefinitionRepository {
    suspend fun getAllDefinitions(): List<WordDefinition>

    suspend fun getWordDefinitionById(wordId: Long): WordDefinition

    suspend fun updateWordDefinition(wordDefinition: WordDefinition)

    suspend fun getDefinitionsByDictionaryId(dictionaryId: Long): List<WordDefinition>

    suspend fun addInDictionary(wordDefinition: WordDefinition, dictionaryId: Long)

    suspend fun deleteDictionary(wordDefinition: WordDefinition, dictionaryId: Long)

    suspend fun deleteWordDefinitionById(wordDefinitionId: Long)

    suspend fun deleteWordDefinitionsById(wordDefinitionIds: List<Long>)
}
