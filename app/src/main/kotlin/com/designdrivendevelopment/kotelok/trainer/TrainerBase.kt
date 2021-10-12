package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord

const val DECREASE_INDEX_COEF = 0.5f

open class TrainerBase(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean,
    private val learnProgress: Float,
) {
    var currentIdx = 0
    var shuffledWords = if (onlyNotLearned) {
        learnableWords.toList().shuffled().filter { it.translation.learntIndex < 1f }
    } else {
        learnableWords.toList().shuffled()
    }
    // number of words which will the trainer give away
    var size = shuffledWords.size

    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size

    private var repeatWordsSet = mutableSetOf<LearnableWord>()

    fun handleTrueAnswer(word: LearnableWord) {
        word.translation.learntIndex = minOf(
            word.translation.learntIndex + learnProgress, 1f)

        currentIdx += 1
        if (currentIdx >= shuffledWords.size) {
            // begin to iterate over words which were guessed incorrectly
            shuffledWords = repeatWordsSet.toList()
            repeatWordsSet = mutableSetOf<LearnableWord>()
            currentIdx = 0
        }
    }

    fun handleFalseAnswer(word: LearnableWord) {
        word.translation.learntIndex = maxOf(
            word.translation.learntIndex - DECREASE_INDEX_COEF * learnProgress, 0f)
        repeatWordsSet.add(word)
    }

    private var previousWordIsChecked = true
    fun handleGetWordFlag() {
        if (!previousWordIsChecked) {
            throw RuntimeException("Check the previous word before to get the nex one")
        }
        previousWordIsChecked = false
    }

    fun handleSetWordFlag() {
        if (previousWordIsChecked) {
            throw RuntimeException("Try to check the same word two times")
        }
        previousWordIsChecked = true
    }
}
