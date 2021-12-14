package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef

@Dao
interface DictionaryWordDefCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dictionaryWordDefCrossRef: DictionaryWordDefCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dictionaryWordDefCrossRefs: List<DictionaryWordDefCrossRef>)

    @Query(
        """
        SELECT *
        FROM dictionary_word_def_cross_refs
        WHERE dict_id == :dictionaryId AND word_def_id == :wordDefinitionId
    """
    )
    suspend fun getCrossRefByDictAndDefIds(
        dictionaryId: Long,
        wordDefinitionId: Long
    ): DictionaryWordDefCrossRef?

    @Query(
        """
        DELETE
        FROM dictionary_word_def_cross_refs
        WHERE ((dict_id = :dictionaryId) AND (word_def_id = :wordDefinitionId))
        """
    )
    suspend fun deleteCrossRefByIds(dictionaryId: Long, wordDefinitionId: Long)

    @Query(
        """
        DELETE
        FROM dictionary_word_def_cross_refs
        WHERE ((dict_id = :dictionaryId) AND (word_def_id IN (:definitionsIds)))
        """
    )
    suspend fun deleteCrossRefsByIds(dictionaryId: Long, definitionsIds: List<Long>)

    @Query(
        """
        DELETE
        FROM dictionary_word_def_cross_refs
        WHERE (dict_id IN (:dictionariesIds))
        """
    )
    suspend fun deleteCrossRefByDictIds(dictionariesIds: List<Long>)

    @Query(
        """
        DELETE
        FROM dictionary_word_def_cross_refs
        WHERE (word_def_id = :wordDefinitionId) AND (dict_id NOT IN (:newDictionariesIds))
        """
    )
    suspend fun deleteRedundantCrossRefsById(
        newDictionariesIds: List<Long>,
        wordDefinitionId: Long
    )
}
