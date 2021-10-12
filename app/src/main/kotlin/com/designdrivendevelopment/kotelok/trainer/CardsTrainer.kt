package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val CARDS_PROGRESS = 0.01f

class CardsTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    TrainerBase(learnableWords, onlyNotLearned, CARDS_PROGRESS) {

    public fun getNextWord(): LearnableWord {
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(isRight: Boolean) {
        if (!isRight) {
            return // don't do anything if the result was not correct
        }
        // the summand depends on the trainer type
        shuffledWords[this.currentIdx].translation.learntIndex += learnProgress
        this.currentIdx += 1
    }
}
