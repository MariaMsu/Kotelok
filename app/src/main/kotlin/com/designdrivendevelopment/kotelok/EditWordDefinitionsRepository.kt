package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface EditWordDefinitionsRepository {
    suspend fun loadDefinitionsByWriting(writing: String): DefinitionsRequestResult

    suspend fun getSavedDefinitionsByWriting(writing: String): List<WordDefinition>

    suspend fun addWordDefinition(wordDefinition: WordDefinition)

    suspend fun updateWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinition(definition: WordDefinition)

    suspend fun deleteWordDefinitions(definitions: List<WordDefinition>)
}
