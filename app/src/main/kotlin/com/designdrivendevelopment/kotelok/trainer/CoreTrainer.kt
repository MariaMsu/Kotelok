package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import java.util.Calendar

abstract class CoreTrainer<NextOutType, CheckInputType>(
    private val learnableDefinitionsRepository: LearnableDefinitionsRepository,
    private val trainerWeight: Float,
) {
    var currentIdx = 0
    var size = 0 // number of words which will the trainer give away
    val isDone: Boolean
        get() = currentIdx >= shuffledWords.size

    var shuffledWords = emptyList<LearnableDefinition>()
    private var repeatWordsSet = mutableSetOf<LearnableDefinition>()

    suspend fun loadDictionary(dictionaryId: Long, onlyNotLearned: Boolean) {
        shuffledWords = if (onlyNotLearned) {
            learnableDefinitionsRepository
                .getByDictionaryId(
                    dictionaryId = dictionaryId,
                )
        } else {
            learnableDefinitionsRepository
                .getByDictionaryIdAndRepeatDate(
                    dictionaryId = dictionaryId,
                    repeatDate = with(Calendar.getInstance()) {
                        time
                    }
                )
        }
        shuffledWords.shuffled()
        repeatWordsSet = mutableSetOf<LearnableDefinition>()

        currentIdx = 0
        size = shuffledWords.size
    }

    fun handleAnswer(word: LearnableDefinition, scoreEF: Int): Boolean {
        word.changeEFBasedOnNewGrade(scoreEF, trainerWeight)
        val isRight = scoreEF >= LearnableDefinition.GRADE_FOUR
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
