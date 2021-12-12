package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.utils.PairCheckInput

const val PAIR_WEIGHT = 0.1f

class TrainerPair(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
    private val setSize: Int = 5,
) :
    CoreTrainer<Pair<List<String>, List<LearnableDefinition>>,
        PairCheckInput>(learnableDefinitionsRepository, changeStatisticsRepository, PAIR_WEIGHT) {

    private var currentWordSubList = listOf<LearnableDefinition>()

    public override fun getNext(): Pair<List<String>, List<LearnableDefinition>> {
        val lastSubsetIdx = minOf(shuffledWords.size, this.currentIdx + setSize)
        val currentWordSubList = shuffledWords.subList(this.currentIdx, lastSubsetIdx).shuffled()
        val words = currentWordSubList.map { it.writing }.shuffled()
        return Pair(words, currentWordSubList)
    }

    public override suspend fun checkUserInput(userInput: PairCheckInput): Boolean {
        // we don't really need to use a map instead of a list to search a word
        // because the list has a small length
        val learnableWorld = currentWordSubList.first { it.writing == userInput.writing }

        val scoreEF = rateEF(learnableWorld, userInput.definitionId)
        return handleAnswer(learnableWorld, scoreEF)
    }

    private fun rateEF(expectedWord: LearnableDefinition, definitionId: Long): Int {
        if (expectedWord.definitionId == definitionId) {
            return 5
        } else {
            return 0
        }
    }
}
