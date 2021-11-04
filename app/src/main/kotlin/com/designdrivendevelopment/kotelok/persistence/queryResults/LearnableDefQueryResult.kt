package com.designdrivendevelopment.kotelok.persistence.queryResults

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import java.util.Date

data class LearnableDefQueryResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "word_id")
    val wordId: Long,
    @ColumnInfo(name = "writing")
    val writing: String,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeechTitle: String?,
    @ColumnInfo(name = "main_translation")
    val mainTranslation: String,
    @ColumnInfo(name = "next_repeat_date")
    val nextRepeatDate: Date,
    @ColumnInfo(name = "repetition_number")
    val repetitionNumber: Int,
    @ColumnInfo(name = "interval")
    val interval: Int,
    @ColumnInfo(name = "easiness_factor")
    val easinessFactor: Float,
    @Relation(
        parentColumn = "part_of_speech",
        entityColumn = "original_title"
    )
    val partOfSpeechEntity: PartOfSpeechEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "word_def_id"
    )
    val translations: List<TranslationEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "word_def_id"
    )
    val exampleEntities: List<ExampleEntity>,
)
