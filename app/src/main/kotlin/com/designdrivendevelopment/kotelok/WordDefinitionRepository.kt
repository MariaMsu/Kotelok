package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface WordDefinitionRepository {
    suspend fun getDefinitionsByDictionaryId(dictionaryId: Long): List<WordDefinition>

    suspend fun getAllDefinitions(): List<WordDefinition>

    suspend fun getWordDefinitionById(wordId: Long): WordDefinition

    suspend fun updateWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinitionById(wordDefinitionId: Long)

    suspend fun deleteWordDefinitionsById(wordDefinitionIds: List<Long>)
}
