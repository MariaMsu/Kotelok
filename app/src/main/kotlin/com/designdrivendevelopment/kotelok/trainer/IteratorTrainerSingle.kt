package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

abstract class IteratorTrainerSingle<CheckInputType>(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean,
    learnProgress: Float,
) :
    CoreTrainer<LearnableWord, CheckInputType>(
        learnableWords,
        onlyNotLearned,
        learnProgress
    ) {
    public override fun getNext(): LearnableWord {
        return shuffledWords[this.currentIdx]
    }

    public override fun checkUserInput(userInput: CheckInputType): Boolean {
        val currWord = shuffledWords[this.currentIdx]
        val isRight = isUserRight(currWord, userInput)
        // incorrect answer
        if (!isRight) {
            handleFalseAnswer(currWord)
            return false
        }
        // correct answer
        handleTrueAnswer(currWord)
        return true
    }

    abstract fun isUserRight(expectedWord: LearnableWord, userInput: CheckInputType): Boolean
}
