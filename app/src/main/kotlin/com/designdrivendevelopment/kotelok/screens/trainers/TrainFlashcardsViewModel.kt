package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainFlashcardsViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository
) : ViewModel() {
    private val _viewState = MutableLiveData(TrainFlashcardsFragment.State.NOT_GUESSED)
    val trainerCards: TrainerCards = TrainerCards(cardsLearnDefRepository)
    val viewState: LiveData<TrainFlashcardsFragment.State> = _viewState
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    val currentWord: LiveData<LearnableDefinition> = _currentWord
    private var dictId: Long = 0

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            // в onlyNotLearned ошибка, он работает наоборот
            trainerCards.loadDictionary(dictionaryId, onlyNotLearned = false)
            val learnableDefinition = trainerCards.getNext()
            _currentWord.postValue(learnableDefinition)
        }
    }

    fun restartDict() {
        _viewState.postValue(TrainFlashcardsFragment.State.NOT_GUESSED)
        viewModelScope.launch(Dispatchers.IO) {
            trainerCards.loadDictionary(dictId, onlyNotLearned = true)
            val learnableDefinition = trainerCards.getNext()
            _currentWord.postValue(learnableDefinition)
        }
    }

    fun onCardPressed(currentState: TrainFlashcardsFragment.State) {
        _viewState.value = when (currentState) {
            TrainFlashcardsFragment.State.NOT_GUESSED -> TrainFlashcardsFragment.State.GUESSED_TRANSLATION
            TrainFlashcardsFragment.State.GUESSED_TRANSLATION -> TrainFlashcardsFragment.State.GUESSED_WORD
            TrainFlashcardsFragment.State.GUESSED_WORD -> TrainFlashcardsFragment.State.GUESSED_TRANSLATION
        }
    }

    fun onGuessPressed(guess: Boolean) {
        _viewState.value = TrainFlashcardsFragment.State.NOT_GUESSED
        trainerCards.checkUserInput(guess)
        if (!trainerCards.isDone) {
            _currentWord.value = trainerCards.getNext()
        }
    }
}
