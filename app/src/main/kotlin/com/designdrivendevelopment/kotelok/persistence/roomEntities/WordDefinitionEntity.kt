package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "word_definitions",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WordDefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "def_id")
    val id: Long,
    @ColumnInfo(name = "word_id")
    val wordId: Long,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,
    @ColumnInfo(name = "transcription")
    val transcription: String,
    @ColumnInfo(name = "main_translation")
    val mainTranslation: String,
    @ColumnInfo(name = "next_repeat_date")
    val nextRepeatDate: Date,
    @ColumnInfo(name = "repetition_number")
    val repetitionNumber: Int,
    @ColumnInfo(name = "last_interval")
    val interval: Int,
    @ColumnInfo(name = "easiness_factor")
    val easinessFactor: Float
)
