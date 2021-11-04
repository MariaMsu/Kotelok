package com.designdrivendevelopment.kotelok.entities

import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@Suppress("LongParameterList")
class LearnableDefinition(
    val definitionId: Long,
    val wordId: Long,
    val writing: String,
    val partOfSpeech: PartOfSpeech?,
    val mainTranslation: String,
    val otherTranslations: List<String>,
    val examples: List<ExampleOfDefinitionUse>,
    nextRepeatDate: Date,
    repetitionNum: Int,
    lastInterval: Int,
    eFactor: Float = EF_INITIAL_VALUE,
) {
    var repeatDate = nextRepeatDate
        private set
    var repetitionNumber = repetitionNum
        private set
    var interval = lastInterval
        private set
    var easinessFactor = eFactor
        private set

    fun changeEFBasedOnNewGrade(
        grade: Int,
        trainerWeight: Float = DEFAULT_COEFFICIENT
    ) {
        if (grade !in GRADE_ZERO..GRADE_FIVE) throw IllegalArgumentException(
            "The quality can only take the following values {0, 1, 2, 3, 4, 5}," +
                " passed value = $grade"
        )

        // Данная формула реализует алгоритм SuperMemo2, константы являются частью формулы алгоритма
        @Suppress("MagicNumber")
        easinessFactor +=
            trainerWeight * (0.1F - (5F - grade) * (0.08F + (5F - grade) * 0.02F))
        if (easinessFactor < EF_MIN_VALUE) easinessFactor = EF_MIN_VALUE

        val calendar = Calendar.getInstance()
        if (grade < PASSING_GRADE) {
            repetitionNumber = 0
            interval = 1
            repeatDate = with(calendar) {
                add(Calendar.DAY_OF_MONTH, interval)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 1)
                time
            }
            return
        }

        interval = when (repetitionNumber) {
            0 -> INITIAL_INTERVAL
            1 -> SECOND_INTERVAL
            else -> (interval * easinessFactor).roundToInt()
        }

        repeatDate = with(calendar) {
            add(Calendar.DAY_OF_MONTH, interval)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 1)
            time
        }
        repetitionNumber ++
    }

    companion object {
        const val GRADE_ZERO = 0
        const val GRADE_ONE = 1
        const val GRADE_TWO = 2
        const val GRADE_THREE = 3
        const val GRADE_FOUR = 4
        const val GRADE_FIVE = 5
        const val PASSING_GRADE = 4
        private const val DEFAULT_COEFFICIENT = 1F
        private const val EF_MIN_VALUE = 1.3F
        private const val INITIAL_INTERVAL = 1
        private const val SECOND_INTERVAL = 6
        const val EF_INITIAL_VALUE = 2.5F
    }
}
