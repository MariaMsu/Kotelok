package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.PartOfSpeech
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity
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

    override suspend fun getWordDefinitionByWordId(
        wordId: Long
    ): WordDefinition = withContext(Dispatchers.IO) {
        wordDefinitionsDao.getDefinitionsByWordId(wordId).toWordDefinition()
    }

//    override suspend fun deleteWordDefinitionById(wordDefinitionId: Long) {
//        wordDefinitionsDao.deleteWordDefinitionById(wordDefinitionId)
//        wordDefCrossRefDao.deleteByWordDefinitionId(wordDefinitionId)
//    }
}

fun WordDefinitionQueryResult.toWordDefinition(): WordDefinition {
    return WordDefinition(
        id = this.id,
        wordId = this.wordId,
        writing = this.writing,
        partOfSpeech = this.partOfSpeechEntity?.toPartOfSpeech(),
        transcription = this.transcription,
        synonyms = this.synonyms.map { synonymEntity -> synonymEntity.writing },
        mainTranslation = this.mainTranslation,
        otherTranslations = this.translations.apply { minus(mainTranslation) }
            .map { translationEntity -> translationEntity.translation },
        examples = this.exampleEntities
            .map { exampleEntity -> exampleEntity.toExampleOfDefinitionUse() },
        nextRepeatDate = this.nextRepeatDate
    )
}

fun PartOfSpeechEntity?.toPartOfSpeech(): PartOfSpeech? {
    if (this == null) {
        return null
    }
    return when (this.russianTitle) {
        PartOfSpeech.NOUN_RU_TITLE -> {
            PartOfSpeech.Noun(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.ADJECTIVE_RU_TITLE -> {
            PartOfSpeech.Adjective(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.ADVERB_RU_TITLE -> {
            PartOfSpeech.Adverb(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.NUMERAL_RU_TITLE -> {
            PartOfSpeech.Numeral(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.PRONOUN_RU_TITLE -> {
            PartOfSpeech.Pronoun(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        PartOfSpeech.VERB_RU_TITLE -> {
            PartOfSpeech.Verb(
                language = this.language,
                originalTitle = this.originalTitle
            )
        }
        else -> {
            PartOfSpeech.Other(
                language = this.language,
                originalTitle = this.originalTitle,
                russianTitle = this.russianTitle
            )
        }
    }
}

fun ExampleEntity.toExampleOfDefinitionUse(): ExampleOfDefinitionUse {
    return ExampleOfDefinitionUse(
        wordDefinitionId = this.wordDefinitionId,
        originalText = this.original,
        translatedText = this.translation.orEmpty()
    )
}
