package com.designdrivendevelopment.kotelok.repositoryImplementations.dictionaryWordDefinitionsRepository

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toWordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen.DictionaryWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DictWordDefinitionRepositoryImpl(
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val crossRefDao: DictionaryWordDefCrossRefDao
) : DictionaryWordDefinitionsRepository {
    override suspend fun getDefinitionsByDictionaryId(
        dictionaryId: Long
    ): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionsByDictId(dictionaryId)
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override fun getDefinitionsFlowByDictId(dictionaryId: Long): Flow<List<WordDefinition>> {
        return wordDefinitionsDao.getDefinitionsFlowByDictId(dictionaryId)
            .map { definitionQueryResults ->
                definitionQueryResults
                    .map { queryResult -> queryResult.toWordDefinition() }
            }
    }

    override suspend fun getAllDefinitions(): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getAllWordDefinitions()
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override suspend fun getWordDefinitionById(
        wordDefinitionId: Long
    ): WordDefinition? = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionById(wordDefinitionId)?.toWordDefinition()
    }

    override suspend fun deleteDefinitionsFromDictionary(
        dictionaryId: Long,
        definitions: List<WordDefinition>
    ) = withContext(Dispatchers.IO) {
        crossRefDao.deleteCrossRefsByIds(
            dictionaryId,
            definitions.map { it.id }
        )
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
    }
}
