package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition

const val WRITE_WEIGHT = 0.15f

class WriteCoreTrainer(
    dictionaryId: Long,
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    onlyNotLearned: Boolean = true,
) :
    IteratorTrainerSingle<String>(dictionaryId,
        learnableDefinitionsRepository,
        onlyNotLearned,
        WRITE_WEIGHT) {

    override fun rateEF(expectedWord: LearnableDefinition, userInput: String): Int {
        // TODO return colored string with a highlighted error
        if (userInput == expectedWord.writing) {
            return 5
        } else {
            return 0
        }
    }
}
