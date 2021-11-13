package com.designdrivendevelopment.kotelok.persistence.roomEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designdrivendevelopment.kotelok.entities.Language
import java.util.Date

@Entity(
    tableName = "word_definitions"
)
data class WordDefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "def_id")
    val id: Long,
    @ColumnInfo(name = "writing")
    val writing: String,
    @ColumnInfo(name = "language")
    val language: Language,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,
    @ColumnInfo(name = "transcription")
    val transcription: String?,
    @ColumnInfo(name = "main_translation")
    val mainTranslation: String,
    @ColumnInfo(name = "cards_next_repeat_date")
    val cardsNextRepeatDate: Date,
    @ColumnInfo(name = "cards_repetition_number")
    val cardsRepetitionNumber: Int,
    @ColumnInfo(name = "cards_last_interval")
    val cardsInterval: Int,
    @ColumnInfo(name = "writer_next_repeat_date")
    val writerRepeatDate: Date,
    @ColumnInfo(name = "writer_repetition_number")
    val writerRepetitionNumber: Int,
    @ColumnInfo(name = "writer_last_interval")
    val writerInterval: Int,
    @ColumnInfo(name = "pairs_next_repeat_date")
    val pairsNextRepeatDate: Date,
    @ColumnInfo(name = "pairs_repetition_number")
    val pairsRepetitionNumber: Int,
    @ColumnInfo(name = "pairs_last_interval")
    val pairsInterval: Int,
    @ColumnInfo(name = "easiness_factor")
    val easinessFactor: Float,
    @ColumnInfo(name = "completed_trainings_number")
    val completedTrainingsNum: Int = 0,
    @ColumnInfo(name = "successfully_trainings_number")
    val successfullyTrainingsNum: Int = 0
)
