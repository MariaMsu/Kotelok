package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val CARDS_PROGRESS = 0.01f

class CardsTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    TrainerBase(learnableWords, onlyNotLearned, CARDS_PROGRESS) {

    public fun getNextWord(): LearnableWord {
        handleGetWordFlag()
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(isRight: Boolean): Boolean {
        handleSetWordFlag()
        val learnableWorld = shuffledWords[this.currentIdx]

        // incorrect answer
        if (!isRight) {
            handleFalseAnswer(learnableWorld)
            return false
        }
        // correct answer
        handleTrueAnswer(learnableWorld)
        return true
    }
}
