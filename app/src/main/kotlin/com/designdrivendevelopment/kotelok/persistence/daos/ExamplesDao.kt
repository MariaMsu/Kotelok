package com.designdrivendevelopment.kotelok.persistence.daos

import androidx.room.Dao
import androidx.room.Insert
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity

@Dao
interface ExamplesDao {
    @Insert
    suspend fun insert(exampleEntity: ExampleEntity)

    @Insert
    suspend fun insert(exampleEntities: List<ExampleEntity>)
}
