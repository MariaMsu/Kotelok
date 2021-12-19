package com.designdrivendevelopment.kotelok.repositoryImplementations.editWordDefnititionsRepository

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.ExamplesDao
import com.designdrivendevelopment.kotelok.persistence.daos.SynonymsDao
import com.designdrivendevelopment.kotelok.persistence.daos.TranslationsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toExampleEntity
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toSynonymEntity
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toTranslationEntity
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toWordDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.toWordDefinitionEntity
import com.designdrivendevelopment.kotelok.screens.dictionaries.EditWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditWordDefRepositoryImpl(
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val dictWordDefCrossRefDao: DictionaryWordDefCrossRefDao,
    private val translationsDao: TranslationsDao,
    private val synonymsDao: SynonymsDao,
    private val examplesDao: ExamplesDao
) : EditWordDefinitionsRepository {
    override suspend fun addWordDefinition(wordDefinition: WordDefinition) {
        if (wordDefinition.id == NEW_WORD_ID) {
            addNewWordDefinition(wordDefinition)
        } else {
            updateWordDefinition(wordDefinition)
        }
    }

    override suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>
    ) {
        if (wordDefinition.id == NEW_WORD_ID) {
            addNewWordDefinition(wordDefinition, dictionaries)
        } else {
            updateWordDefinition(wordDefinition, dictionaries)
        }
    }

    override suspend fun addDefinitionsToDictionary(
        definitions: List<WordDefinition>,
        dictionary: Dictionary
    ) = withContext(Dispatchers.IO) {
        val savedDefinitions = definitions.filter { it.id != NEW_WORD_ID }
        val loadedDefinitions = definitions.filter { it.id == NEW_WORD_ID }

        addCrossRefs(savedDefinitions, dictionary.id)
        loadedDefinitions.forEach { definition ->
            addNewWordDefinition(definition, listOf(dictionary))
        }
    }

    override suspend fun deleteWordDefinition(wordDefinition: WordDefinition) {
        wordDefinitionsDao.deleteWordDefinitionById(wordDefinition.id)
    }

    override suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>) {
        wordDefinitionsDao.deleteWordDefinitionsByIds(wordDefinitions.map { it.id })
    }

    override suspend fun copyDefinitionToDictionary(
        wordDefinition: WordDefinition,
        dictionary: Dictionary
    ): Boolean = withContext(Dispatchers.IO) {
        val existingDefinition =
            wordDefinitionsDao.getDefinitionById(wordDefinition.id)?.toWordDefinition()
        return@withContext if (existingDefinition == wordDefinition) {
            val crossRef =
                dictWordDefCrossRefDao.getCrossRefByDictAndDefIds(dictionary.id, wordDefinition.id)
            if (crossRef == null) {
                addCrossRefs(wordDefinition.id, listOf(dictionary))
                false
            } else {
                true
            }
        } else {
            addNewWordDefinitionWithDictionaries(
                wordDefinition.copy(id = NEW_WORD_ID),
                listOf(dictionary)
            )
            false
        }
    }

    private suspend fun addNewWordDefinition(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>? = null
    ): Long {
        val definitionId = wordDefinitionsDao.insert(wordDefinition.toWordDefinitionEntity())
        val synonymEntities = wordDefinition.synonyms.map { synonym ->
            synonym.toSynonymEntity(definitionId)
        }
        val translationEntities = wordDefinition.otherTranslations.map { translation ->
            translation.toTranslationEntity(definitionId)
        }
        val exampleEntities = wordDefinition.examples.map { exampleOfDefinitionUse ->
            exampleOfDefinitionUse.toExampleEntity(definitionId)
        }

        synonymsDao.insert(synonymEntities)
        translationsDao.insert(translationEntities)
        examplesDao.insert(exampleEntities)

        if (dictionaries != null) {
            addCrossRefs(definitionId, dictionaries)
        }
        return definitionId
    }

    private suspend fun updateWordDefinition(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>? = null
    ) {
        val definitionId = wordDefinition.id
        wordDefinitionsDao.updateWordDefinitionAttributes(
            wordDefinitionId = definitionId,
            writing = wordDefinition.writing,
            language = wordDefinition.language,
            partOfSpeech = wordDefinition.partOfSpeech,
            transcription = wordDefinition.transcription,
            mainTranslation = wordDefinition.mainTranslation
        )

        val synonymEntities = wordDefinition.synonyms.map { synonym ->
            synonym.toSynonymEntity(definitionId)
        }
        val translationEntities = wordDefinition.otherTranslations.map { translation ->
            translation.toTranslationEntity(definitionId)
        }
        val exampleEntities = wordDefinition.examples.map { exampleOfDefinitionUse ->
            exampleOfDefinitionUse.toExampleEntity(definitionId)
        }

        synonymsDao.insert(synonymEntities)
        synonymsDao.deleteRedundantSynonyms(wordDefinition.id, wordDefinition.synonyms)

        translationsDao.insert(translationEntities)
        translationsDao.deleteRedundantTranslations(wordDefinition.id, wordDefinition.otherTranslations)

        examplesDao.insert(exampleEntities)
        examplesDao.update(exampleEntities)
        examplesDao.deleteRedundantExamples(
            wordDefinition.id,
            wordDefinition.examples.map { it.originalText }
        )

        if (dictionaries != null) {
            addCrossRefs(wordDefinition.id, dictionaries)
            dictWordDefCrossRefDao.deleteRedundantCrossRefsById(
                newDictionariesIds = dictionaries.map { it.id },
                wordDefinitionId = wordDefinition.id
            )
        }
    }

    private suspend fun addCrossRefs(definitions: List<WordDefinition>, dictionaryId: Long) {
        val crossRefs = definitions.map { definition ->
            DictionaryWordDefCrossRef(
                dictionaryId = dictionaryId,
                wordDefinitionId = definition.id
            )
        }
        dictWordDefCrossRefDao.insert(crossRefs)
    }

    private suspend fun addCrossRefs(definitionId: Long, dictionaries: List<Dictionary>) {
        val crossRefs = dictionaries.map { dictionary ->
            DictionaryWordDefCrossRef(
                dictionaryId = dictionary.id,
                wordDefinitionId = definitionId
            )
        }
        dictWordDefCrossRefDao.insert(crossRefs)
    }

    companion object {
        private const val NEW_WORD_ID = 0L
    }
}
