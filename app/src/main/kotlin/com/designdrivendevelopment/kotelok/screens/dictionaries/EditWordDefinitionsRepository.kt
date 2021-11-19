package com.designdrivendevelopment.kotelok.screens.dictionaries

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface EditWordDefinitionsRepository {
    suspend fun addWordDefinition(wordDefinition: WordDefinition)

    suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>
    )

    suspend fun deleteWordDefinition(wordDefinition: WordDefinition)

    suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>)
}
