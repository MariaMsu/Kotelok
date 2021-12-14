package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDefinitionsDao {
    @Insert
    suspend fun insert(wordDefinitionEntity: WordDefinitionEntity): Long

    // В данной функции нельзя уменьшить число аргументов, т. к. она реализует SQL-запрос
    @Suppress("LongParameterList")
    @Query(
        """
        UPDATE word_definitions
        SET writing = :writing,
            language = :language,
            part_of_speech = :partOfSpeech,
            transcription = :transcription,
            main_translation =:mainTranslation
        WHERE (def_id = :wordDefinitionId)
    """
    )
    suspend fun updateWordDefinitionAttributes(
        wordDefinitionId: Long,
        writing: String,
        language: Language,
        partOfSpeech: String?,
        transcription: String?,
        mainTranslation: String,
    )

    @Transaction
    @Query(
        """
        SELECT def_id AS id, writing, part_of_speech, language,
        transcription, main_translation
        FROM word_definitions
        WHERE (def_id = :wordDefinitionId)
    """
    )
    suspend fun getDefinitionById(wordDefinitionId: Long): WordDefinitionQueryResult?

    @Transaction
    @Query(
        """
        SELECT def_id AS id, writing, part_of_speech, language,
        transcription, main_translation
        FROM word_definitions
        WHERE (writing = :writing)
    """
    )
    fun getFlowOfDefinitionsByWriting(writing: String): Flow<List<WordDefinitionQueryResult>>

    @Transaction
    @Query(
        """
        SELECT def_id AS id, writing, part_of_speech, language,
        transcription, main_translation
        FROM word_definitions AS words_d
        WHERE (words_d.def_id IN (
            SELECT cross_refs.word_def_id
            FROM dictionary_word_def_cross_refs AS cross_refs
            WHERE (cross_refs.dict_id = :dictionaryId)
        ))
    """
    )
    suspend fun getDefinitionsByDictId(dictionaryId: Long): List<WordDefinitionQueryResult>

    @Transaction
    @Query(
        """
        SELECT def_id AS id, writing, part_of_speech, language,
        transcription, main_translation
        FROM word_definitions AS words_d
        WHERE (words_d.def_id IN (
            SELECT cross_refs.word_def_id
            FROM dictionary_word_def_cross_refs AS cross_refs
            WHERE (cross_refs.dict_id = :dictionaryId)
        ))
    """
    )
    fun getDefinitionsFlowByDictId(dictionaryId: Long): Flow<List<WordDefinitionQueryResult>>

    @Transaction
    @Query(
        """
        SELECT def_id AS id, writing, part_of_speech, language,
        transcription, main_translation
        FROM word_definitions
        """
    )
    suspend fun getAllWordDefinitions(): List<WordDefinitionQueryResult>

    @Query("DELETE FROM word_definitions WHERE (def_id = :wordDefinitionId)")
    suspend fun deleteWordDefinitionById(wordDefinitionId: Long)

    @Query("DELETE FROM word_definitions WHERE (def_id IN (:wordDefinitionIds))")
    suspend fun deleteWordDefinitionsByIds(wordDefinitionIds: List<Long>)

    @Query(
        """
            DELETE
            FROM word_definitions
            WHERE def_id IN (
                SELECT w_d.def_id
                FROM word_definitions AS w_d LEFT OUTER JOIN dictionary_word_def_cross_refs AS c_r
                ON (w_d.def_id = c_r.word_def_id) WHERE c_r.dict_id IS NULL
            )
        """
    )
    suspend fun deleteDefinitionsWithoutDict()
}
