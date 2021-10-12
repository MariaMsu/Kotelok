package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val WRITE_PROGRESS = 0.15f

class WriteTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    TrainerBase(learnableWords, onlyNotLearned, WRITE_PROGRESS) {
    var previousWordIsChecked = true

    public fun getNextWord(): LearnableWord {
        if (!previousWordIsChecked) {
            throw RuntimeException("Check the previous word before to get the nex one")
        }
        previousWordIsChecked = false
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(userString: String): String {
        if (previousWordIsChecked) {
            throw RuntimeException("Try to check the same word two times")
        }
        previousWordIsChecked = true

        val learnableWorld = shuffledWords[this.currentIdx]

        // incorrect answer
        if (userString != learnableWorld.writing) {
            // TODO return colored string with a highlighted error
            handleFalseAnswer(learnableWorld)
            return userString // don't do anything if the result was not correct
        }
        // correct answer
        handleTrueAnswer(learnableWorld)
        return userString
    }
}
