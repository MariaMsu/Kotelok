package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition

abstract class IteratorTrainerSingle<CheckInputType>(
    dictionaryId: Long,
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    onlyNotLearned: Boolean,
    trainerWeight: Float,
) :
    CoreTrainer<LearnableDefinition, CheckInputType>(
        dictionaryId,
        learnableDefinitionsRepository,
        onlyNotLearned,
        trainerWeight
    ) {
    public override fun getNext(): LearnableDefinition {
        return shuffledWords[this.currentIdx]
    }

    public override fun checkUserInput(userInput: CheckInputType): Boolean {
        val currWord = shuffledWords[this.currentIdx]
        val scoreEF = rateEF(currWord, userInput)
        return handleAnswer(currWord, scoreEF)
    }

    abstract fun rateEF(expectedWord: LearnableDefinition, userInput: CheckInputType): Int
}
