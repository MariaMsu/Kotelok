package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.utils.levenshteinDifference
import kotlin.math.roundToInt

enum class StrChange {
    KEEP, INSERT, REPLACE, DELETE
}
typealias WordChangeArray = Array<Pair<Char, StrChange>>

const val WRITE_WEIGHT = 0.15f

class TrainerWriter(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
) :
    IteratorTrainerSingle<String>(learnableDefinitionsRepository, WRITE_WEIGHT) {

    /*
    expectedStr: cats
    userStr: cut
    curWordChange: [(c, KEEP), (a, REPLACE), (t, KEEP), (s, INSERT)]
     */
    public var curWordChange: WordChangeArray = emptyArray()
        private set

    override fun rateEF(expectedWord: LearnableDefinition, userInput: String): Int {
        val (levenshteinDistance, path) = levenshteinDifference(
            expectedStr = expectedWord.writing,
            userStr = userInput
        )
        curWordChange = path
        // levenshteinDistance ∈ [0, max(expectedStr.length, userStr.length)]
        val errorRate = levenshteinDistance.toDouble() / expectedWord.writing.length
        val correctRate = (1 - minOf(1.0, errorRate)) // ∈ [0, 1]
        val normalizedCorrectRate = correctRate * (LearnableDefinition.GRADE_ARRAY.size - 1)

        return LearnableDefinition.GRADE_ARRAY[normalizedCorrectRate.roundToInt()]
    }
}
