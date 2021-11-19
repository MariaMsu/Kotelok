package com.designdrivendevelopment.kotelok.repositoryImplementations.dictionaryWordDefinitionsRepository

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.repositoryImplementations.toWordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionaryWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictWordDefinitionRepositoryImpl(
    private val wordDefinitionsDao: WordDefinitionsDao
) : DictionaryWordDefinitionsRepository {
    override suspend fun getDefinitionsByDictionaryId(
        dictionaryId: Long
    ): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionsByDictId(dictionaryId)
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override suspend fun getAllDefinitions(): List<WordDefinition> = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getAllWordDefinitions()
            .map { queryResult -> queryResult.toWordDefinition() }
    }

    override suspend fun getWordDefinitionById(
        wordDefinitionId: Long
    ): WordDefinition = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionById(wordDefinitionId).toWordDefinition()
    }

//    override suspend fun deleteWordDefinitionById(wordDefinitionId: Long) {
//        wordDefinitionsDao.deleteWordDefinitionById(wordDefinitionId)
//        wordDefCrossRefDao.deleteByWordDefinitionId(wordDefinitionId)
//    }
}
