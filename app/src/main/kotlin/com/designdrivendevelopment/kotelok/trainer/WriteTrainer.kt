package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val WRITE_PROGRESS = 0.15f

class WriteTrainer(learnableWords: List<LearnableWord>, onlyNotLearned: Boolean = true) :
    TrainerBase(learnableWords, onlyNotLearned, WRITE_PROGRESS) {

    public fun getNextWord(): LearnableWord {
        return shuffledWords[this.currentIdx]
    }

    public fun setUserInput(userString: String): String {
        if (userString != shuffledWords[this.currentIdx].writing) {
            // TODO return colored string with a highlighted error
            return userString // don't do anything if the result was not correct
        }
        // the summand depends on the trainer type
        shuffledWords[this.currentIdx].translation.learntIndex += learnProgress
        this.currentIdx += 1
        return userString
    }
}
