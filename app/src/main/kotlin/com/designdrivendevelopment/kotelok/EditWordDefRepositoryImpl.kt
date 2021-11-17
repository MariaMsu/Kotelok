package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.ExamplesDao
import com.designdrivendevelopment.kotelok.persistence.daos.SynonymsDao
import com.designdrivendevelopment.kotelok.persistence.daos.TranslationsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.yandexDictApi.YandexDictionaryApiService
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.YandexDictionaryResponse
import java.net.UnknownHostException
import retrofit2.HttpException

class EditWordDefRepositoryImpl(
    private val yandexDictApiService: YandexDictionaryApiService,
    private val wordDefinitionsDao: WordDefinitionsDao,
    private val dictWordDefCrossRefDao: DictionaryWordDefCrossRefDao,
    private val translationsDao: TranslationsDao,
    private val synonymsDao: SynonymsDao,
    private val examplesDao: ExamplesDao
) : EditWordDefinitionsRepository {
    // Отключаю проверку для данной функции, так как здесь в любом случае будет присутствовать не
    // менее 3х return (два из них возвразают результат обработки исключения, а один возвращает
    // результат в случае успеха)
    @Suppress("ReturnCount")
    override suspend fun loadDefinitionsByWriting(writing: String): DefinitionsRequestResult {
        try {
            val response: YandexDictionaryResponse = yandexDictApiService.lookupWord(writing)
            if (response.definitions.isEmpty()) {
                return DefinitionsRequestResult.EmptyResult()
            }
            val definitionsList: List<WordDefinition> = response
                .definitions.flatMap { definitionResponse ->
                    definitionResponse.translations.map { translationResponse ->
                        translationResponse.toWordDefinition(
                            definitionResponse.writing,
                            definitionResponse.transcription
                        )
                    }
                }
            return DefinitionsRequestResult.Success(definitionsList)
        } catch (e: UnknownHostException) {
            return DefinitionsRequestResult.Failure.Error("UnknownHostException")
        } catch (e: HttpException) {
            return DefinitionsRequestResult.Failure.HttpError(
                code = e.code(),
                message = e.message()
            )
        }
    }

    override suspend fun getSavedDefinitionsByWriting(writing: String): List<WordDefinition> {
        TODO("Not yet implemented")
    }

    override suspend fun addWordDefinition(wordDefinition: WordDefinition) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewWordDefinitionWithDictionaries(
        wordDefinition: WordDefinition,
        dictionaries: List<Dictionary>,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWordDefinition(wordDefinition: WordDefinition) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWordDefinitions(wordDefinitions: List<WordDefinition>) {
        TODO("Not yet implemented")
    }
}
