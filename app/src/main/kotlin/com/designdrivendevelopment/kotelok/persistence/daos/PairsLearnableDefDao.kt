package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.designdrivendevelopment.kotelok.persistence.queryResults.LearnableDefQueryResult

@Dao
interface PairsLearnableDefDao {
    @Query(
        """
        UPDATE word_definitions
        SET pairs_next_repeat_date = :nextRepeatDateInMillis,
            pairs_repetition_number = :repetitionNumber,
            pairs_last_interval = :interval,
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
        SELECT def_id AS id, writing, part_of_speech, main_translation,
        pairs_next_repeat_date AS next_repeat_date, pairs_repetition_number AS repetition_number,
        pairs_last_interval AS interval, easiness_factor
        FROM word_definitions AS words_d
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
        SELECT def_id AS id, writing, part_of_speech, main_translation,
        pairs_next_repeat_date AS next_repeat_date, pairs_repetition_number AS repetition_number,
        pairs_last_interval AS interval, easiness_factor
        FROM word_definitions
        WHERE (:repeatDateInMillis >= pairs_next_repeat_date)
    """
    )
    suspend fun getLearnableDefinitionsByDate(
        repeatDateInMillis: Long
    ): List<LearnableDefQueryResult>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, writing, part_of_speech, main_translation,
        pairs_next_repeat_date AS next_repeat_date, pairs_repetition_number AS repetition_number,
        pairs_last_interval AS interval, easiness_factor
        FROM word_definitions AS words_d
        WHERE ((:repeatDateInMillis >= words_d.pairs_next_repeat_date) AND (words_d.def_id IN (
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
        SELECT def_id AS id, writing, part_of_speech, main_translation,
        pairs_next_repeat_date AS next_repeat_date, pairs_repetition_number AS repetition_number,
        pairs_last_interval AS interval, easiness_factor
        FROM word_definitions AS words_d
        """
    )
    suspend fun getAllLearnableDefinitions(): List<LearnableDefQueryResult>
}
