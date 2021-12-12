package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.screens.trainers.LearnableDefinitionsRepository

const val CARDS_WEIGHT = 0.4f

class TrainerCards(
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) :
    IteratorTrainerSingle<Boolean>(
        learnableDefinitionsRepository,
        changeStatisticsRepository,
        CARDS_WEIGHT
    ) {

    override fun rateEF(expectedWord: LearnableDefinition, userInput: Boolean): Int {
        return if (userInput) {
            LearnableDefinition.GRADE_FOUR
        } else {
            0
        }
    }
}
