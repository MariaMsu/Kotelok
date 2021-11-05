package com.designdrivendevelopment.kotelok.persistence.prepopulating
import android.content.Context

import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.DefinitionResponse
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.TranslationResponse
import com.designdrivendevelopment.kotelok.yandexDictApi.responses.YandexDictionaryResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Calendar

class AssetsRepository(private val context: Context) {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun readDefinitionsFromAssets(): List<DefinitionResponse> {
        val definitionsJson = readAssetFileToString("data.json")
        val prepopulateData = jsonFormat
            .decodeFromString<List<YandexDictionaryResponse>>(definitionsJson)
        return prepopulateData.flatMap { it.definitions }
    }

    private fun readAssetFileToString(fileName: String): String {
        val stream = context.assets.open(fileName)
        return stream.bufferedReader().readText()
    }
}

fun TranslationResponse.getWordDefinitionEntity(
    writing: String,
    transcription: String?
): WordDefinitionEntity {
    return WordDefinitionEntity(
        id = 0,
        writing = writing,
        partOfSpeech = this.partOfSpeech.toRuPosOrNull(),
        transcription = transcription,
        language = Language.ENG,
        mainTranslation = translation,
        nextRepeatDate = with(Calendar.getInstance()) {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 1)
            time
        },
        repetitionNumber = 0,
        interval = 1,
        easinessFactor = 2.5F
    )
}

fun TranslationResponse.getSynonymEntities(defId: Long): List<SynonymEntity> {
    return synonyms?.map { synonymResponse ->
        SynonymEntity(defId, synonymResponse.writing)
    }.orEmpty()
}

fun String?.toRuPosOrNull(): String? {
    return when (this) {
        "adverb" -> "нар."
        "verb" -> "гл."
        "adjective" -> "прил."
        "pronoun" -> "мест."
        "noun" -> "сущ."
        "numeral" -> "числ."
        else -> null
    }
}

fun TranslationResponse.getTranslationEntities(defId: Long): List<TranslationEntity> {
    return (
        otherTranslations
            ?.map { otherTranslationResponse ->
                otherTranslationResponse.writing
            }.orEmpty() + translation
        ).map { translation ->
        TranslationEntity(defId, translation)
    }
}

fun TranslationResponse.getExampleEntities(defId: Long): List<ExampleEntity> {
    return examples?.map { exampleResponse ->
        ExampleEntity(
            wordDefinitionId = defId,
            original = exampleResponse.original,
            translation = exampleResponse.translations?.first()?.translation
        )
    }.orEmpty()
}
