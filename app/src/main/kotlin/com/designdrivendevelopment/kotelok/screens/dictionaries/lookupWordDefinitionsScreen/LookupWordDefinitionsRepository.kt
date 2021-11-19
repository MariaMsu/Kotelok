package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import kotlinx.coroutines.flow.Flow

interface LookupWordDefinitionsRepository {
    fun loadDefinitionsByWriting(writing: String): Flow<DefinitionsRequestResult>

    fun getSavedDefinitionsByWriting(writing: String): Flow<List<WordDefinition>>
}
