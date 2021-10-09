package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

class Cards(learnableWords: List<LearnableWord>, onlyNotLearned: Boolean = true) {
    private var currentIdx = 0
    private val shuffledWords = if (onlyNotLearned) {
        learnableWords.toMutableList().shuffled().filter { it.translation.learntIndex < 1f }
    } else {
        learnableWords.toMutableList().shuffled()
    }
    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size

    public fun getNextWord(): LearnableWord {
        return shuffledWords[this.currentIdx]
    }

    public fun setCurrentResult(isRight: Boolean) {
        if (!isRight) {
            return // don't do anything if the result was not correct
        }
        // the summand depends on the trainer type
        shuffledWords[this.currentIdx].translation.learntIndex += RENAME_ME
        this.currentIdx += 1
        print(this.currentIdx)
    }

    companion object {
        private const val  RENAME_ME = 0.1f // TODO
    }
}
