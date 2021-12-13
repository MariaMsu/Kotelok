package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TrainerTest {
    private val mockLDR = MockLDR()
    private val mockCSR = MockCSR()

    @Test
    fun cardsTest() = runBlocking {
        val data = mockLDR.getAll()
        val trainer = TrainerCards(
            learnableDefinitionsRepository = mockLDR,
            changeStatisticsRepository = mockCSR
        )
        trainer.loadDictionary(dictionaryId = 69, onlyNotLearned = false)
        assertEquals(data.size, trainer.size)
        var counter = 0
        while (!trainer.isDone) {
            val learnableDefinition: LearnableDefinition = trainer.getNext()
            val correctUserInput = true
            assertTrue(trainer.checkUserInput(correctUserInput))
            counter += 1
        }
        assertEquals(data.size, counter)
    }

    @Test
    fun writerTest() = runBlocking {
        val data = mockLDR.getAll()
        val trainer = TrainerWriter(
            learnableDefinitionsRepository = mockLDR,
            changeStatisticsRepository = mockCSR
        )
        trainer.loadDictionary(dictionaryId = 69, onlyNotLearned = false)
        assertEquals(data.size, trainer.size)
        var counter = 0
        while (!trainer.isDone) {
            val learnableDefinition: LearnableDefinition = trainer.getNext()
            val correctUserInput = learnableDefinition.writing
            assertTrue(trainer.checkUserInput(correctUserInput))
            counter += 1
        }
        assertEquals(data.size, counter)
    }
}
