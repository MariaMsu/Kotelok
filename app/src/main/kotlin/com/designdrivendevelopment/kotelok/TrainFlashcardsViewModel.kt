package com.designdrivendevelopment.kotelok

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import kotlinx.coroutines.launch

class TrainFlashcardsViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository
) : ViewModel() {
    var state: MutableLiveData<TrainFlashcardsFragment.State> = MutableLiveData<TrainFlashcardsFragment.State>()
    var trainerCards: TrainerCards? = null
    var currentWord: MutableLiveData<LearnableDefinition>? = null

    init {
        state.value = TrainFlashcardsFragment.State.NOT_GUESSED
        loadTrainerCardsById(dictionaryId, cardsLearnDefRepository)
    }

    fun loadTrainerCardsById(dictionaryId: Long, cardsLearnDefRepository: CardsLearnableDefinitionsRepository) {
        trainerCards = TrainerCards(cardsLearnDefRepository)
        viewModelScope.launch {
            trainerCards?.loadDictionary(
                dictionaryId,
                false
            )
            currentWord?.value = trainerCards?.getNext()
        }
    }
}
