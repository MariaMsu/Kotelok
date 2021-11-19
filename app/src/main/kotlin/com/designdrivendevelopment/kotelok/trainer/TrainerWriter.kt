package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository

const val WRITE_WEIGHT = 0.15f

class WriteCoreTrainer(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
) :
    IteratorTrainerSingle<String>(learnableDefinitionsRepository, WRITE_WEIGHT) {

    override fun rateEF(expectedWord: LearnableDefinition, userInput: String): Int {
        // TODO return colored string with a highlighted error
        if (userInput == expectedWord.writing) {
            return 5
        } else {
            return 0
        }
    }
}
