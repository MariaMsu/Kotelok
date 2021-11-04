package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.designdrivendevelopment.kotelok.persistence.queryResults.LearnableDefQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionQueryResult
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity

@Suppress("TooManyFunctions")
@Dao
interface WordDefinitionsDao {
    @Insert
    suspend fun insert(wordDefinitionEntity: WordDefinitionEntity): Long

    @Query(
        """
        UPDATE word_definitions
        SET next_repeat_date = :nextRepeatDateInMillis,
            repetition_number = :repetitionNumber,
            last_interval = :interval,
            easiness_factor = :easinessFactor
        WHERE (def_id = :wordDefinitionId)
    """
    )
    suspend fun updateWordDefinition(
        wordDefinitionId: Long,
        nextRepeatDateInMillis: Long,
        repetitionNumber: Int,
        interval: Int,
        easinessFactor: Float
    )

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        transcription, main_translation, next_repeat_date
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        WHERE (words_d.def_id = :wordDefinitionId)
    """
    )
    suspend fun getDefinitionById(wordDefinitionId: Long): WordDefinitionQueryResult

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        transcription, main_translation, next_repeat_date
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        WHERE (words_d.word_id = :wordId)
    """
    )
    suspend fun getDefinitionsByWordId(wordId: Long): WordDefinitionQueryResult

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        transcription, main_translation, next_repeat_date
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
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
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        main_translation, next_repeat_date, repetition_number, last_interval AS interval,
        easiness_factor
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        WHERE (words_d.def_id IN (
            SELECT cross_refs.word_def_id
            FROM dictionary_word_def_cross_refs AS cross_refs
            WHERE (cross_refs.dict_id = :dictionaryId)
        ))
    """
    )
    suspend fun getLearnableDefinitionsByDictId(
        dictionaryId: Long
    ): List<LearnableDefQueryResult>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        main_translation, next_repeat_date, repetition_number, last_interval AS interval,
        easiness_factor
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        WHERE (:repeatDateInMillis >= words_d.next_repeat_date)
    """
    )
    suspend fun getLearnableDefinitionsByDate(
        repeatDateInMillis: Long
    ): List<LearnableDefQueryResult>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        main_translation, next_repeat_date, repetition_number, last_interval AS interval,
        easiness_factor
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        WHERE ((:repeatDateInMillis >= words_d.next_repeat_date) AND (words_d.def_id IN (
            SELECT cross_refs.word_def_id
            FROM dictionary_word_def_cross_refs AS cross_refs
            WHERE (cross_refs.dict_id = :dictionaryId)
        )))
    """
    )
    suspend fun getLearnableDefinitionsByDateAndDictId(
        repeatDateInMillis: Long,
        dictionaryId: Long
    ): List<LearnableDefQueryResult>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        transcription, main_translation, next_repeat_date
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        """
    )
    suspend fun getAllWordDefinitions(): List<WordDefinitionQueryResult>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, part_of_speech,
        main_translation, next_repeat_date, repetition_number, last_interval AS interval,
        easiness_factor
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        """
    )
    suspend fun getAllLearnableDefinitions(): List<LearnableDefQueryResult>

    @Query("DELETE FROM word_definitions WHERE (def_id = :wordDefinitionId)")
    suspend fun deleteWordDefinitionById(wordDefinitionId: Long)
}
