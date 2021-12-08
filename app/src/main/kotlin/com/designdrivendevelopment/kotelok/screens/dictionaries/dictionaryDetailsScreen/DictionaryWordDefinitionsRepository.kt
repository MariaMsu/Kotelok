package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import kotlinx.coroutines.flow.Flow

interface DictionaryWordDefinitionsRepository {
    suspend fun getDefinitionsByDictionaryId(dictionaryId: Long): List<WordDefinition>

    fun getDefinitionsFlowByDictId(dictionaryId: Long): Flow<List<WordDefinition>>

    suspend fun getAllDefinitions(): List<WordDefinition>

    suspend fun getWordDefinitionById(wordDefinitionId: Long): WordDefinition?
}
