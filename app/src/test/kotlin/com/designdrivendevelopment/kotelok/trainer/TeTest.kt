package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TeTest {
    private val mockLDR = MockLDR()


    @Test
    fun cardsTest() = runBlocking{
        val data = mockLDR.getAll()
        val trainer = TrainerCards(learnableDefinitionsRepository=mockLDR)
        trainer.loadDictionary(dictionaryId = 69, onlyNotLearned = false)
        assertEquals(data.size, trainer.size)
        while (!trainer.isDone){
            val learnableDefinition : LearnableDefinition = trainer.getNext()
            val correctUserInput = true
            assertTrue(trainer.checkUserInput(correctUserInput))
        }
    }
}
