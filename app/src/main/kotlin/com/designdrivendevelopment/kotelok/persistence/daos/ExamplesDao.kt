package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity

@Dao
interface ExamplesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exampleEntity: ExampleEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exampleEntities: List<ExampleEntity>)

    @Query(
        """
            DELETE
            FROM examples
            WHERE (word_def_id = :definitionId) AND original_example NOT IN (:newExamples)
        """
    )
    suspend fun deleteRedundantExamples(definitionId: Long, newExamples: List<String>)
}
