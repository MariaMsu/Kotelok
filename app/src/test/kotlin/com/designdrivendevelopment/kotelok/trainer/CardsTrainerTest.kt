package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import com.designdrivendevelopment.kotelok.trainer.entities.Translation
import org.junit.Assert.assertEquals
import org.junit.Test

class CardsTrainerTest {

    private fun getDictionaryData(): List<LearnableWord> {
        return listOf(
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
    }

    @Test
    fun cards() {
        val dictionaryData = getDictionaryData()
        val trainer = CardsTrainer(dictionaryData)
        val firstWord = trainer.getNextWord()
        for (i in 0..3) {
            // if the word was incorrect then the method should return the same word again
            trainer.setUserInput(isRight = false)
            assertEquals(trainer.getNextWord(), firstWord)
        }
        var i = 0
        while (!trainer.isDone) {
            trainer.getNextWord()
            trainer.setUserInput(isRight = true)
            i += 1
        }
        assertEquals(dictionaryData.size, i)
    }
}
