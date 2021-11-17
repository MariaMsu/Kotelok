package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface EditWordDefinitionsRepository {
    suspend fun loadDefinitionsByWriting(writing: String): DefinitionsRequestResult

    suspend fun getSavedDefinitionsByWriting(writing: String): List<WordDefinition>

    suspend fun addWordDefinition(wordDefinition: WordDefinition)

    suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>
    )

    suspend fun deleteWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>)
}
