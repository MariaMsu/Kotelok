package com.designdrivendevelopment.kotelok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import kotlinx.coroutines.launch

class TrainFlashcardsViewModel (
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository
    ): ViewModel() {
    var state: TrainFlashcardsFragment.State = TrainFlashcardsFragment.State.NOT_GUESSED
    var trainerCards : TrainerCards? = null
    var currentWord: LearnableDefinition? = null

    init {
        loadTrainerCardsById(dictionaryId, cardsLearnDefRepository)
    }

    fun loadTrainerCardsById(dictionaryId: Long, cardsLearnDefRepository: CardsLearnableDefinitionsRepository) {
        trainerCards = TrainerCards(cardsLearnDefRepository)
        viewModelScope.launch {
            trainerCards?.loadDictionary(
                dictionaryId,
                false
            )
            currentWord = trainerCards?.getNext()
        }
    }
}
