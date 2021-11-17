package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.ExamplesDao
import com.designdrivendevelopment.kotelok.persistence.daos.SynonymsDao
import com.designdrivendevelopment.kotelok.persistence.daos.TranslationsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.kotelok.yandexDictApi.YandexDictionaryApiService
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.YandexDictionaryResponse
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class EditWordDefRepositoryImpl(
    private val yandexDictApiService: YandexDictionaryApiService,
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val dictWordDefCrossRefDao: DictionaryWordDefCrossRefDao,
    private val translationsDao: TranslationsDao,
    private val synonymsDao: SynonymsDao,
    private val examplesDao: ExamplesDao
) : EditWordDefinitionsRepository {
    override fun loadDefinitionsByWriting(
        writing: String
    ): Flow<DefinitionsRequestResult> = flow {
        try {
            val response: YandexDictionaryResponse = yandexDictApiService.lookupWord(writing)
            val definitionsList: List<WordDefinition> = response
                .definitions.flatMap { definitionResponse ->
                    definitionResponse.translations.map { translationResponse ->
                        translationResponse.toWordDefinition(
                            definitionResponse.writing,
                            definitionResponse.transcription
                        )
                    }
                }
            emit(DefinitionsRequestResult.Success(definitionsList))
        } catch (e: UnknownHostException) {
            emit(DefinitionsRequestResult.Failure.Error("UnknownHostException"))
        } catch (e: HttpException) {
            emit(
                DefinitionsRequestResult.Failure.HttpError(
                    code = e.code(),
                    message = e.message()
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    override fun getSavedDefinitionsByWriting(
        writing: String
    ): Flow<List<WordDefinition>> {
        return wordDefinitionsDao.getFlowOfDefinitionsByWriting(writing).map { definitionsList ->
            definitionsList.map { wordDefinitionQueryResult ->
                wordDefinitionQueryResult.toWordDefinition()
            }
        }
    }

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

    override suspend fun deleteWordDefinition(wordDefinition: WordDefinition) {
        wordDefinitionsDao.deleteWordDefinitionById(wordDefinition.id)
    }

    override suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>) {
        wordDefinitionsDao.deleteWordDefinitionsByIds(wordDefinitions.map { it.id })
    }

    private suspend fun addNewWordDefinition(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>? = null
    ): Long {
        val definitionId = wordDefinitionsDao.insert(wordDefinition.toWordDefinitionEntity())
        val synonymEntities = wordDefinition.synonyms.map { synonym ->
            SynonymEntity(
                wordDefinitionId = definitionId,
                writing = synonym
            )
        }
        val translationEntities = wordDefinition.allTranslations.map { translation ->
            TranslationEntity(
                wordDefinitionId = definitionId,
                translation = translation
            )
        }
        val exampleEntities = wordDefinition.examples.map { exampleOfDefinitionUse ->
            ExampleEntity(
                wordDefinitionId = definitionId,
                original = exampleOfDefinitionUse.originalText,
                translation = exampleOfDefinitionUse.translatedText
            )
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
        wordDefinitionsDao.updateWordDefinitionAttributes(
            wordDefinitionId = wordDefinition.id,
            writing = wordDefinition.writing,
            language = wordDefinition.language,
            partOfSpeech = wordDefinition.partOfSpeech,
            transcription = wordDefinition.transcription,
            mainTranslation = wordDefinition.mainTranslation
        )

        val synonymEntities = wordDefinition.synonyms.map { synonym ->
            SynonymEntity(
                wordDefinitionId = wordDefinition.id,
                writing = synonym
            )
        }
        val translationEntities = wordDefinition.allTranslations.map { translation ->
            TranslationEntity(
                wordDefinitionId = wordDefinition.id,
                translation = translation
            )
        }
        val exampleEntities = wordDefinition.examples.map { exampleOfDefinitionUse ->
            ExampleEntity(
                wordDefinitionId = wordDefinition.id,
                original = exampleOfDefinitionUse.originalText,
                translation = exampleOfDefinitionUse.translatedText
            )
        }

        synonymsDao.insert(synonymEntities)
        synonymsDao.deleteRedundantSynonyms(wordDefinition.id, wordDefinition.synonyms)

        translationsDao.insert(translationEntities)
        translationsDao.deleteRedundantTranslations(wordDefinition.id, wordDefinition.allTranslations)

        examplesDao.insert(exampleEntities)
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
