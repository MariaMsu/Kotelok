package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val WRITE_PROGRESS = 0.15f

class WriteTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    TrainerBase(learnableWords, onlyNotLearned, WRITE_PROGRESS) {

    public fun getNextWord(): LearnableWord {
        handleGetWordFlag()
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(userString: String): String {
        handleSetWordFlag()
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
