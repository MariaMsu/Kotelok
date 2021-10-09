package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import com.designdrivendevelopment.kotelok.trainer.entities.Translation
import org.junit.Assert.assertEquals
import org.junit.Test

class CardsTest {

    @Test
    fun cards() {
        val lWords = listOf(
            LearnableWord(
                0,
                "dog",
                Translation(
                    id = 20,
                    description = listOf(""),
                    transcription = "",
                    examples = listOf(""),
                    learntIndex = 0.2f
                )
            ),
            LearnableWord(
                1,
                "cat",
                Translation(
                    id = 13,
                    description = listOf(""),
                    transcription = "",
                    examples = listOf(""),
                    learntIndex = 0.4f
                )
            ),
            LearnableWord(
                2,
                "bird",
                Translation(
                    id = 58,
                    description = listOf(""),
                    transcription = "",
                    examples = listOf(""),
                    learntIndex = 0.4f
                )
            ),
        )
        val c = Cards(lWords)
        val firstWord = c.getNextWord()
        for (i in 0..3) {
            // if the word was incorrect then the method should return the same word again
            c.setCurrentResult(isRight = false)
            assertEquals(c.getNextWord(), firstWord)
        }
        var i = 0
        while (!c.isDone) {
            c.getNextWord()
            c.setCurrentResult(isRight = true)
            i += 1
        }
        assertEquals(lWords.size, i)
    }
}
