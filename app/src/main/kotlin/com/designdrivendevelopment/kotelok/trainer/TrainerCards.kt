package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val CARDS_PROGRESS = 0.01f

class TrainerCards(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
) :
    IteratorTrainerSingle<Boolean>(learnableWords, onlyNotLearned, CARDS_PROGRESS) {

    override fun isUserRight(expectedWord: LearnableWord, userInput: Boolean): Boolean {
        return userInput
    }
}
