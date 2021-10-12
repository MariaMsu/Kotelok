package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

open class TrainerBase(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean,
    val learnProgress: Float,
) {
    var currentIdx = 0
    val shuffledWords = if (onlyNotLearned) {
        learnableWords.toMutableList().shuffled().filter { it.translation.learntIndex < 1f }
    } else {
        learnableWords.toMutableList().shuffled()
    }

    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size
}
