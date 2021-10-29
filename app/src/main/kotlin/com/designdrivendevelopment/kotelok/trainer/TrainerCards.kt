package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.LearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition

const val CARDS_WEIGHT = 0.01f

class TrainerCards(
    dictionaryId: Long,
    learnableDefinitionsRepository: LearnableDefinitionsRepository,
    onlyNotLearned: Boolean = true,
) :
    IteratorTrainerSingle<Boolean>(dictionaryId,learnableDefinitionsRepository, onlyNotLearned, CARDS_WEIGHT) {

    override fun rateEF(expectedWord: LearnableDefinition, userInput: Boolean): Int {
        return if (userInput){
            LearnableDefinition.DEFAULT_QUALITY
        }else{
            0
        }
    }
}
