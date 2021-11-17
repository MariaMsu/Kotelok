package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import kotlinx.coroutines.flow.Flow

interface EditWordDefinitionsRepository {
    fun loadDefinitionsByWriting(writing: String): Flow<DefinitionsRequestResult>

    fun getSavedDefinitionsByWriting(writing: String): Flow<List<WordDefinition>>

    suspend fun addWordDefinition(wordDefinition: WordDefinition)

    suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>
    )

    suspend fun deleteWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>)
}
