package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.utils.StrChange
import com.designdrivendevelopment.kotelok.trainer.utils.WordChangeArray
import com.designdrivendevelopment.kotelok.trainer.utils.levenshteinDifference
import junit.framework.TestCase.assertEquals
import org.junit.Test

data class LevenshteinTestCase(
    val expectedStr: String,
    val userStr: String,
    val levenshteinDistance: Int,
    var levenshteinDifference: WordChangeArray,
)

class LevenshteinTest {
    private val tests = arrayOf(
        LevenshteinTestCase(
            expectedStr = "dog",
            userStr = "dog",
            levenshteinDistance = 0,
            levenshteinDifference = arrayOf(
                Pair('d', StrChange.KEEP),
                Pair('o', StrChange.KEEP),
                Pair('g', StrChange.KEEP)
            )
        ),
        LevenshteinTestCase(
            expectedStr = "dog",
            userStr = "",
            levenshteinDistance = 3,
            levenshteinDifference = arrayOf(
                Pair('d', StrChange.INSERT),
                Pair('o', StrChange.INSERT),
                Pair('g', StrChange.INSERT)
            )
        ),
        LevenshteinTestCase(
            expectedStr = "o",
            userStr = "dog",
            levenshteinDistance = 2,
            levenshteinDifference = arrayOf(
                Pair('d', StrChange.DELETE),
                Pair('o', StrChange.KEEP),
                Pair('g', StrChange.DELETE)
            )
        ),
        LevenshteinTestCase(
            expectedStr = "deceptive",
            userStr = "dcepltiv",
            levenshteinDistance = 3,
            levenshteinDifference = arrayOf(
                Pair('d', StrChange.KEEP),
                Pair('e', StrChange.INSERT),
                Pair('c', StrChange.KEEP),
                Pair('e', StrChange.KEEP),
                Pair('p', StrChange.KEEP),
                Pair('l', StrChange.DELETE),
                Pair('t', StrChange.KEEP),
                Pair('i', StrChange.KEEP),
                Pair('v', StrChange.KEEP),
                Pair('e', StrChange.INSERT),
            ),
        )
    )

    @Test
    fun levenshteinTestFun() {

        for (testCase in tests) {
            val (levDistance, levDifference) = levenshteinDifference(
                expectedStr = testCase.expectedStr,
                userStr = testCase.userStr
            )
            assertEquals(testCase.levenshteinDistance, levDistance)
            assertEquals(testCase.levenshteinDifference.size, levDifference.size)
            for (i in testCase.levenshteinDifference.indices) {
                assertEquals(testCase.levenshteinDifference[i], levDifference[i])
            }
        }
    }
}
