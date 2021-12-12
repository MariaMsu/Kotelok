package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainFlashcardsViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModel() {
    private val _viewState = MutableLiveData(TrainFlashcardsFragment.State.NOT_GUESSED)
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    private val _isTrainerDone: MutableLiveData<Boolean> = MutableLiveData()
    private var dictId: Long = 0
    val trainerCards: TrainerCards = TrainerCards(cardsLearnDefRepository, changeStatisticsRepository)
    val viewState: LiveData<TrainFlashcardsFragment.State> = _viewState
    val currentWord: LiveData<LearnableDefinition> = _currentWord
    val isTrainerDone: LiveData<Boolean>
        get() = _isTrainerDone

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            // в onlyNotLearned ошибка, он работает наоборот
            trainerCards.loadDictionary(dictionaryId, onlyNotLearned = false)
            val isDone = trainerCards.isDone
            _isTrainerDone.postValue(isDone)
            if (!isDone) {
                _currentWord.postValue(trainerCards.getNext())
            }
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
        viewModelScope.launch(Dispatchers.IO) {
            trainerCards.checkUserInput(guess)

            _viewState.postValue(TrainFlashcardsFragment.State.NOT_GUESSED)
            val isDone = trainerCards.isDone
            _isTrainerDone.postValue(isDone)
            if (!isDone) {
                _currentWord.postValue(trainerCards.getNext())
            }
        }
    }
}
