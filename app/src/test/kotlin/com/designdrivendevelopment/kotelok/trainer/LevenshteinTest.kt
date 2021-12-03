package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.utils.StrChange
import com.designdrivendevelopment.kotelok.trainer.utils.WordChangeArray
import com.designdrivendevelopment.kotelok.trainer.utils.levenshteinDifference
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Test

data class LevenshteinTestCase(
    val expectedStr: String,
    val userStr: String,
    val levenshteinDistance: Int,
    var levenshteinDifference: WordChangeArray,
)

class LevenshteinTest {
    @Test
    fun levenshteinTest() {
        val tests = arrayOf(
            LevenshteinTestCase(expectedStr = "dog",
                userStr = "dog",
                levenshteinDistance = 0,
                levenshteinDifference = arrayOf(
                    Pair('d', StrChange.KEEP),
                    Pair('o', StrChange.KEEP),
                    Pair('g', StrChange.KEEP))
            )
        )

        for (testCase in tests) {
            val (levDistance, levDifference) = levenshteinDifference(
                expectedStr = testCase.expectedStr,
                userStr = testCase.userStr
            )
            assertEquals(testCase.levenshteinDistance, levDistance)
            assertEquals(testCase.levenshteinDifference.size, levDifference.size)
            for (i in testCase.levenshteinDifference.indices){
                assertEquals(testCase.levenshteinDifference[i], levDifference[i])
            }
        }
    }
}
