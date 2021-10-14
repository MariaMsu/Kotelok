package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val WRITE_PROGRESS = 0.15f

class WriteCoreTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    IteratorTrainerSingle<String>(learnableWords, onlyNotLearned, WRITE_PROGRESS) {

    override fun isUserRight(expectedWord: LearnableWord, userInput: String): Boolean {
        // TODO return colored string with a highlighted error
        return userInput == expectedWord.writing
    }
}
