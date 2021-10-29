package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import java.util.*

abstract class CoreTrainer<NextOutType, CheckInputType>(
    dictionaryId: Long,
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    onlyNotLearned: Boolean,
    private val trainerWeight: Float,
) {

    var shuffledWords: List<LearnableDefinition>

    init {
        shuffledWords = if (onlyNotLearned) {
            learnableDefinitionsRepository
                .getLearnableDefinitionsByDictionaryId(
                    dictionaryId = dictionaryId,
                )
        } else {
            learnableDefinitionsRepository
                .getLearnableDefinitionsByDictionaryIdAndRepeatDate(
                    dictionaryId = dictionaryId,
                    repeatDate = with(Calendar.getInstance()) {
                        time
                    }
                )
        }
        shuffledWords.shuffled()
    }

    var currentIdx = 0

    // number of words which will the trainer give away
    var size = shuffledWords.size

    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size

    private var repeatWordsSet = mutableSetOf<LearnableDefinition>()

    fun handleAnswer(word: LearnableDefinition, scoreEF: Int): Boolean {
        word.changeEFBasedOnNewQuality(scoreEF, trainerWeight)
        val isRight = scoreEF >= LearnableDefinition.DEFAULT_QUALITY
        if (!isRight) {
            repeatWordsSet.add(word)
        }

        currentIdx += 1
        if (currentIdx >= shuffledWords.size) {
            // begin to iterate over words which were guessed incorrectly
            shuffledWords = repeatWordsSet.toList()
            repeatWordsSet = mutableSetOf<LearnableDefinition>()
            currentIdx = 0
        }

        return isRight
    }

    /* returns the data for training */
    public abstract fun getNext(): NextOutType

    /* checks user userInput and calls the methods
    'handleTrueAnswer()' and 'handleFalseAnswer()' inside itself */
    public abstract fun checkUserInput(userInput: CheckInputType): Boolean
}
