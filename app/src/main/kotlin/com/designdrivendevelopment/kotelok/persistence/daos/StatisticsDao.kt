package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.queryResults.DictStatQueryResult
import com.designdrivendevelopment.kotelok.persistence.queryResults.WordDefinitionStatQuery

@Dao
interface StatisticsDao {
    @Query(
        """
        SELECT d.id AS id, d.label AS label, AVG(w_d.easiness_factor) AS average_skill_level,
        SUM(w_d.def_id) AS size, SUM(w_d.completed_trainings_number) AS completed_num,
        SUM(w_d.successfully_trainings_number) AS successfully_num
        FROM dictionaries AS d LEFT OUTER JOIN dictionary_word_def_cross_refs AS c_r
        ON (d.id = c_r.dict_id)
        LEFT OUTER JOIN word_definitions AS w_d ON (c_r.word_def_id = w_d.def_id)
        GROUP BY d.id
    """
    )
    suspend fun getStatisticForAllDictionaries(): List<DictStatQueryResult>

    @Query(
        """
        SELECT d.id AS id, d.label AS label, AVG(w_d.easiness_factor) AS average_skill_level,
        SUM(w_d.def_id) AS size, SUM(w_d.completed_trainings_number) AS completed_num,
        SUM(w_d.successfully_trainings_number) AS successfully_num
        FROM dictionaries AS d JOIN dictionary_word_def_cross_refs AS c_r ON (d.id = c_r.dict_id)
        JOIN word_definitions AS w_d ON (c_r.word_def_id = w_d.def_id)
        WHERE (d.id = :dictionaryId)
    """
    )
    suspend fun getTotalStatisticByDictionaryId(dictionaryId: Long): DictStatQueryResult

    @Query(
        """
        SELECT w_d.def_id AS id, w_d.writing AS writing, w_d.easiness_factor AS skill_level,
        w_d.completed_trainings_number AS completed_num,
        w_d.successfully_trainings_number AS successfully_num
        FROM dictionary_word_def_cross_refs AS c_r
        JOIN word_definitions AS w_d ON (c_r.word_def_id = w_d.def_id)
        WHERE (c_r.dict_id = :dictionaryId)
    """
    )
    suspend fun getWordDefinitionsStatsByDictId(dictionaryId: Long): List<WordDefinitionStatQuery>

    @Query(
        """
        UPDATE word_definitions
        SET completed_trainings_number = completed_trainings_number + 1,
            successfully_trainings_number = CASE
                WHEN :isTrainingResultSuccess = 0 THEN successfully_trainings_number
                ELSE successfully_trainings_number + 1 END
        WHERE (def_id = :wordDefinitionId)
    """
    )
    suspend fun updateWordDefinitionStat(
        wordDefinitionId: Long,
        isTrainingResultSuccess: Int
    )
}
