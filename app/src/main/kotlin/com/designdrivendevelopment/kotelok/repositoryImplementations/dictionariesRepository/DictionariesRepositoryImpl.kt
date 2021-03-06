package com.designdrivendevelopment.kotelok.repositoryImplementations.dictionariesRepository

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionariesDao
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toDictionary
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toDictionaryEntity
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DictionariesRepositoryImpl(
    private val dictionariesDao: DictionariesDao,
    private val dictionaryWordDefCrossRefDao: DictionaryWordDefCrossRefDao,
    private val wordDefinitionsDao: WordDefinitionsDao
) : DictionariesRepository {
    override suspend fun getAllDictionaries(): List<Dictionary> = withContext(Dispatchers.IO) {
        dictionariesDao.getAll().map { dictionaryEntity ->
            val size = dictionariesDao.getDictionarySizeById(dictionaryEntity.id)
            dictionaryEntity.toDictionary(size)
        }
    }

    override fun getAllDictionariesFlow(): Flow<List<Dictionary>> {
        return dictionariesDao.getAllFlowInQueries().map { results ->
            results.map {
                Dictionary(
                    id = it.id,
                    label = it.label,
                    size = it.size,
                    isFavorite = it.isFavorite
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getDictionaryById(
        dictionaryId: Long
    ): Dictionary = withContext(Dispatchers.IO) {
        val dictionaryEntity = dictionariesDao.getDictionaryById(dictionaryId)
        val size = dictionariesDao.getDictionarySizeById(dictionaryEntity.id)
        dictionaryEntity.toDictionary(size)
    }

    override suspend fun addDictionary(
        dictionary: Dictionary,
        addedWordDefinitions: List<WordDefinition>?
    ): Long = withContext(Dispatchers.IO) {
        val dictionaryId = dictionariesDao.insert(dictionary.toDictionaryEntity(entityId = 0))
        if (addedWordDefinitions != null) {
            val crossRefs = addedWordDefinitions.map { wordDefinition ->
                DictionaryWordDefCrossRef(
                    dictionaryId = dictionaryId,
                    wordDefinitionId = wordDefinition.id
                )
            }
            dictionaryWordDefCrossRefDao.insert(crossRefs)
        }
        return@withContext dictionaryId
    }

    override suspend fun updateDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.update(dictionary.toDictionaryEntity(dictionary.id))
    }

    override suspend fun updateDictionaries(dictionaries: List<Dictionary>) = withContext(Dispatchers.IO) {
        dictionariesDao.update(dictionaries.map { it.toDictionaryEntity(it.id) })
    }

    override suspend fun deleteDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.deleteById(dictionary.id)
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
//        dictionaryWordDefCrossRefDao.deleteByDictionaryId(dictionary.id)
    }

    override suspend fun deleteDictionaries(dictionaries: List<Dictionary>) {
        dictionariesDao.deleteByIds(dictionaries.map { it.id })
        dictionaryWordDefCrossRefDao.deleteCrossRefByDictIds(dictionaries.map { it.id })
        wordDefinitionsDao.deleteDefinitionsWithoutDict()
    }

    override suspend fun deleteWordDefinitionFromDictionary(
        dictionary: Dictionary,
        wordDefinition: WordDefinition
    ) = withContext(Dispatchers.IO) {
        dictionaryWordDefCrossRefDao.deleteCrossRefByIds(
            dictionaryId = dictionary.id,
            wordDefinitionId = wordDefinition.id
        )
    }
}
