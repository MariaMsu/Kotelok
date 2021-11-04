package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionariesDao
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionariesRepositoryImpl(
    private val dictionariesDao: DictionariesDao,
    private val dictionaryWordDefCrossRefDao: DictionaryWordDefCrossRefDao
) : DictionariesRepository {
    override suspend fun getAllDictionaries(): List<Dictionary> = withContext(Dispatchers.IO) {
        dictionariesDao.getAll().map { dictionaryEntity ->
            val size = dictionariesDao.getDictionarySizeById(dictionaryEntity.id)
            dictionaryEntity.toDictionary(size)
        }
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
    ) = withContext(Dispatchers.IO) {
        dictionariesDao.insert(dictionary.toDictionaryEntity())
        if (addedWordDefinitions != null) {
            val crossRefs = addedWordDefinitions.map { wordDefinition ->
                DictionaryWordDefCrossRef(
                    dictionaryId = dictionary.id,
                    wordDefinitionId = wordDefinition.id
                )
            }
            dictionaryWordDefCrossRefDao.insert(crossRefs)
        }
    }

    override suspend fun updateDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.update(dictionary.toDictionaryEntity())
    }

    override suspend fun deleteDictionary(dictionary: Dictionary) = withContext(Dispatchers.IO) {
        dictionariesDao.deleteById(dictionary.id)
//        dictionaryWordDefCrossRefDao.deleteByDictionaryId(dictionary.id)
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

fun Dictionary.toDictionaryEntity(): DictionaryEntity {
    return DictionaryEntity(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite
    )
}

fun DictionaryEntity.toDictionary(size: Int): Dictionary {
    return Dictionary(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite,
        size = size
    )
}
