package com.designdrivendevelopment.kotelok.entities

import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@Suppress("LongParameterList")
class LearnableDefinition(
    val definitionId: Long,
    val wordId: Long,
    val writing: String,
    val partOfSpeech: PartOfSpeech,
    val mainTranslation: String,
    val otherTranslations: List<String>,
    val examples: List<ExampleOfDefinitionUse>,
    nextRepeatDate: Date,
    private var repetitionNumber: Int,
    private var interval: Int,
    private var easinessFactor: Float = EF_INITIAL_VALUE,
) {
    var repeatDate = nextRepeatDate
        private set

    fun changeEFBasedOnNewQuality(quality: Int) {
        if (quality !in MIN_QUALITY..MAX_QUALITY) throw IllegalArgumentException(
            "The quality can only take the following values {0, 1, 2, 3, 4, 5}," +
                " passed value = $quality"
        )

        // Данная формула реализует алгоритм SuperMemo2, константы являются частью алгоритма
        @Suppress("MagicNumber")
        easinessFactor += (0.1F - (5F - quality) * (0.08F + (5F - quality) * 0.02F))
        if (easinessFactor < EF_MIN_VALUE) easinessFactor = EF_MIN_VALUE

        val calendar = Calendar.getInstance()
        if (quality < PASSING_GRADE) {
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
        private const val DEFAULT_COEFFICIENT = 1F
        private const val EF_MIN_VALUE = 1.3F
        private const val MIN_QUALITY = 0
        private const val MAX_QUALITY = 5
        private const val PASSING_GRADE = 3
        private const val INITIAL_INTERVAL = 1
        private const val SECOND_INTERVAL = 6
        const val EF_INITIAL_VALUE = 2.5F
        const val DEFAULT_QUALITY = 4
    }
}
