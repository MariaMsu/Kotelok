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
        if (isRight) {
            handleTrueAnswer(currWord)
            return true
        }
        handleFalseAnswer(currWord)
        return false
    }

    abstract fun isUserRight(expectedWord: LearnableWord, userInput: CheckInputType): Boolean
}
