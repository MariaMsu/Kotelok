package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import com.designdrivendevelopment.kotelok.trainer.entities.Translation
import com.designdrivendevelopment.kotelok.trainer.utils.PairCheckInput

const val PAIR_PROGRESS = 0.1f

class PairCoreTrainer(
    learnableWords: List<LearnableWord>,
    onlyNotLearned: Boolean = true,
    private val setSize: Int = 5,
) :
    CoreTrainer<Pair<List<String>, List<Translation>>, PairCheckInput>(learnableWords, onlyNotLearned, PAIR_PROGRESS) {

    private var currentWordSubList = listOf<LearnableWord>()

    public override fun getNext(): Pair<List<String>, List<Translation>> {
        val lastSubsetIdx = minOf(shuffledWords.size, this.currentIdx + setSize)
        val currentWordSubList = shuffledWords.subList(this.currentIdx, lastSubsetIdx)
        val words = currentWordSubList.map { it.writing }.shuffled()
        val translations = currentWordSubList.map { it.translation }.shuffled()
        return Pair(words, translations)
    }

    public override fun checkUserInput(userInput: PairCheckInput): Boolean {
        val originWriting = userInput.originWriting
        val translationId = userInput.translationId
        // we don't really need to use a map instead of a set to search a word
        // because the list has a small length
        val learnableWorld = currentWordSubList.first { it.writing == originWriting }

        val isRight = isUserRight(learnableWorld, translationId)
        if (isRight) {
            handleTrueAnswer(learnableWorld)
            return true
        }
        handleFalseAnswer(learnableWorld)
        return false
    }

    private fun isUserRight(expectedWord: LearnableWord, translationId: Long): Boolean {
        return expectedWord.translation.id == translationId
    }
}
