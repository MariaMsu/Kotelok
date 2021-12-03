package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository
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
        val normalizedCorrectRate = correctRate * LearnableDefinition.GRADE_ARRAY.size

        return LearnableDefinition.GRADE_ARRAY[normalizedCorrectRate.roundToInt()]
    }

    private fun levenshteinDifference(expectedStr: String, userStr: String):
        Pair<Int, WordChangeArray> {
        /* costMatrix:
                  E X P E
            [[m m m m m m]
             [m 0 1 2 3 4]
           U [m 1 * * * *]
           S [m 2 * * * *]
           E [m 3 * * * d]], where
         m = userStr.length + expectedStr.length
         'm' is necessary to compute wordChange in more easy way
         'd' = levenshteinDistance
        */
            val maxCost = userStr.length + expectedStr.length // unreal big cost
            val expectedLen = expectedStr.length + 2
            val userLen = userStr.length + 2

            val costMatrix = Array(userLen) { IntArray(expectedLen) }
            costMatrix[0] = IntArray(expectedLen) { maxCost } // to be it easier to handle wordChange
            costMatrix[1] = IntArray(expectedLen) { it - 1 }
            for (i in 1 until userLen) {
                costMatrix[i][0] = maxCost // to be it easier to handle wordChange
                costMatrix[i][1] = i - 1
            }

            for (i in 2 until userLen) {
                for (j in 2 until expectedLen) {
                    val match = if (expectedStr[j - 2] == userStr[i - 2]) 0 else 1

                    val costReplace = costMatrix[i - 1][j - 1] + match
                    val costInsert = costMatrix[i - 1][j] + 1
                    val costDelete = costMatrix[i][j - 1] + 1

                    costMatrix[i][j] = minOf(costInsert, costDelete, costReplace)
                }
            }
//    for (i in 0 until userLen) {
//        for (j in 0 until expectedLen) {
//            print("${costMatrix[i][j]} ")
//        }
//        print("\n")
//    }
            val levenshteinDistance = costMatrix[userLen - 1][expectedLen - 1]

            var wordChange: WordChangeArray = emptyArray()
            var i = userLen - 1
            var j = expectedLen - 1
            while ((i != 1) || (j != 1)) {

                val costReplace = costMatrix[i - 1][j - 1]
                val costDelete = costMatrix[i - 1][j] + 1
                val costInsert = costMatrix[i][j - 1] + 1

                when (minOf(costInsert, costDelete, costReplace)) {
                    costDelete -> {
                        wordChange += Pair(userStr[i - 2], StrChange.DELETE)
                        i -= 1
                    }

                    costInsert -> {
                        wordChange += Pair(expectedStr[j - 2], StrChange.INSERT)
                        j -= 1
                    }

                    costReplace -> {
                        val strChange = if (expectedStr[j - 2] == userStr[i - 2]) StrChange.KEEP else StrChange.REPLACE
                        wordChange += Pair(expectedStr[j - 2], strChange)
                        i -= 1
                        j -= 1
                    }
                }
            }
            wordChange = wordChange.reversedArray()
            return Pair(levenshteinDistance, wordChange)
        }
}
