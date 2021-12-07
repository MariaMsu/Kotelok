package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.TrainerWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainWriteViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository
) : ViewModel() {
    private var dictId: Long = 0
    private val _viewState = MutableLiveData(TrainWriteFragment.State.NOT_GUESSED)
    val viewState: LiveData<TrainWriteFragment.State> = _viewState
    val trainerWriter: TrainerWriter = TrainerWriter(cardsLearnDefRepository)
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    val currentWord: LiveData<LearnableDefinition> = _currentWord

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            trainerWriter.loadDictionary(dictionaryId, onlyNotLearned = false)
            val learnableDefinition = trainerWriter.getNext()
            _currentWord.postValue(learnableDefinition)
        }
    }

    fun restartDict() {
        _viewState.postValue(TrainWriteFragment.State.NOT_GUESSED)
        viewModelScope.launch(Dispatchers.IO) {
            trainerWriter.loadDictionary(dictId, onlyNotLearned = true)
            val learnableDefinition = trainerWriter.getNext()
            _currentWord.postValue(learnableDefinition)
        }
    }

    fun onPressNext() {
        _viewState.value = TrainWriteFragment.State.NOT_GUESSED
        if (!trainerWriter.isDone) {
            _currentWord.value = trainerWriter.getNext()
        }
    }

    fun onGuess(guess: String) {
        val res = trainerWriter.checkUserInput(guess)
        _viewState.value = if (res) TrainWriteFragment.State.GUESSED_CORRECT
        else TrainWriteFragment.State.GUESSED_INCORRECT
    }
}
