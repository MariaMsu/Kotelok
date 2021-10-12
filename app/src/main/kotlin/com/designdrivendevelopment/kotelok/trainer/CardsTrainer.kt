package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val CARDS_PROGRESS = 0.01f

class CardsTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    TrainerBase(learnableWords, onlyNotLearned, CARDS_PROGRESS) {

    var previousWordIsChecked = true

    public fun getNextWord(): LearnableWord {
        if (! previousWordIsChecked){
            throw RuntimeException("Check the previous word before to get the nex one")
        }
        previousWordIsChecked = false
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(isRight: Boolean) : Boolean {
        if (previousWordIsChecked){
            throw RuntimeException("Try to check the same word two times")
        }
        previousWordIsChecked = true

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
