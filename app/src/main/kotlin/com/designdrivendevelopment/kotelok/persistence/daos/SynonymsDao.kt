package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity

@Dao
interface SynonymsDao {
    @Insert
    suspend fun insert(synonymEntity: SynonymEntity)

    @Insert
    suspend fun insert(synonymEntities: List<SynonymEntity>)

    @Query(
        """
            DELETE
            FROM synonyms
            WHERE (word_def_id = :definitionId) AND synonyms.writing NOT IN (:newSynonyms)
        """
    )
    suspend fun deleteRedundantSynonyms(definitionId: Long, newSynonyms: List<String>)
}
