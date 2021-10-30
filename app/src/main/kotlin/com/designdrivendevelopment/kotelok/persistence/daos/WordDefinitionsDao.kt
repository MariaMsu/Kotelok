package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.designdrivendevelopment.kotelok.persistence.dto.WordDef
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity

@Dao
interface WordDefinitionsDao {
    @Insert
    suspend fun insert(wordDefinitionEntity: WordDefinitionEntity): Long

    @Query(
        """SELECT *
        FROM word_definitions
        WHERE word_id = :wordId AND main_translation = :mainTranslation"""
    )
    suspend fun getWordDefinitionByWordIdAndMainTranslation(
        wordId: Long,
        mainTranslation: String
    ): List<WordDefinitionEntity>

    @Query(
        """
        SELECT *
        FROM word_definitions
        WHERE (def_id IN (
            SELECT cross_refs.word_def_id
            FROM dictionary_word_def_cross_refs AS cross_refs
            WHERE (cross_refs.dict_id = :dictionaryId)
        ))
    """
    )
    suspend fun getALlDefinitionsByDictId(dictionaryId: Long): List<WordDefinitionEntity>

    @Transaction
    @Query(
        """
        SELECT words_d.def_id AS id, word_id, words.writing AS writing, language, part_of_speech,
        transcription, main_translation, next_repeat_date
        FROM word_definitions AS words_d
        JOIN words ON (words_d.word_id = words.id)
        """
    )
    suspend fun getAll(): List<WordDef>
}
