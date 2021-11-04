package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef

@Dao
interface DictionaryWordDefCrossRefDao {
    @Insert
    suspend fun insert(dictionaryWordDefCrossRef: DictionaryWordDefCrossRef)

    @Insert
    suspend fun insert(dictionaryWordDefCrossRefs: List<DictionaryWordDefCrossRef>)

    @Query(
        """
        DELETE
        FROM dictionary_word_def_cross_refs
        WHERE ((dict_id = :dictionaryId) AND (word_def_id = :wordDefinitionId))
        """
    )
    suspend fun deleteCrossRefByIds(dictionaryId: Long, wordDefinitionId: Long)
}
