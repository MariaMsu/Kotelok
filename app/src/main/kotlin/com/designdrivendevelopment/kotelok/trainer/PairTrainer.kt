package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import com.designdrivendevelopment.kotelok.trainer.entities.Translation

const val PAIR_PROGRESS = 0.1f

class PairTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
    private val setSize: Int = 5,
) :
    TrainerBase(learnableWords, onlyNotLearned, PAIR_PROGRESS) {
    // todo может, на этапе компиляции фиксировать setSize => не с листами, а с массивами
    private var currentWordSet = emptySet<LearnableWord>()

    public fun getNextWordList(): Pair<List<String>, List<Translation>> {
        if (currentWordSet.isNotEmpty()) {
            throw TrainerIterationException(
                "The world ${currentWordSet.map { it.writing }} " +
                    "are remained from previous words list"
            )
        }
        val lastSubsetIdx = minOf(shuffledWords.size, this.currentIdx + setSize)
        val currentWordSubList = shuffledWords.subList(this.currentIdx, lastSubsetIdx)

        val words = currentWordSubList.map { it.writing }.shuffled()
        val translations = currentWordSubList.map { it.translation }.shuffled()

        currentWordSet = currentWordSubList.toMutableSet()
        return Pair(words, translations)
    }

    public fun setUserInput(writing: String, translation: Translation): Boolean {
        // we don't really need to use a map instead of a set to search a word
        // because the list has a small length
        val learnableWorldIdx = currentWordSet.indexOfFirst { it.writing == writing }
        if (learnableWorldIdx == -1) {
            throw TrainerIterationException(
                "Try to set user input to a not existing word or to an already handled word." +
                    "Tried key: $writing. " +
                    "Existed keys: ${currentWordSet.map { it.writing }}"
            )
        }
        val learnableWorld = currentWordSet.elementAt(learnableWorldIdx)

        // incorrect answer
        if (learnableWorld.translation != translation) {
            handleFalseAnswer(learnableWorld)
            return false
        }

        // correct answer
        currentWordSet.drop(learnableWorldIdx)
        handleTrueAnswer(learnableWorld)
        return true
    }
}
