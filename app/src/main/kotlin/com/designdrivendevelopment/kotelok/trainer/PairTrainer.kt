package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import com.designdrivendevelopment.kotelok.trainer.entities.Translation

const val PAIR_PROGRESS = 0.1f

// TODO: может, на этапе компиляции фиксировать setSize
// => не с листами, а с массивами
class PairTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
    private val setSize: Int = 5,
) :
    TrainerBase(learnableWords, onlyNotLearned, PAIR_PROGRESS) {
    private var currentWordSubList: List<LearnableWord>? = null

    public fun getNextWord(): Pair<List<String>, List<Translation>> {
        val lastSubsetIdx = minOf(shuffledWords.size, this.currentIdx + setSize)
        currentWordSubList = shuffledWords.subList(this.currentIdx, lastSubsetIdx)

        val words = currentWordSubList!!.map { it.writing }.shuffled()
        val translations = currentWordSubList!!.map { it.translation }.shuffled()

        return Pair(words, translations)
    }

    public fun setUserInput(word: String, translation: Translation): Boolean {
        // we don't really need to use a map instead of list to search a word
        // because the list has a small length
        val learnableWorld = currentWordSubList!!.find { it.writing == word }
        if (learnableWorld!!.translation != translation) {
            return false// don't do anything if the result was not correct
        } else {
            learnableWorld.translation.learntIndex += learnProgress
            this.currentIdx += 1
            return true
        }
    }
}
