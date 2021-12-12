package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository
import com.designdrivendevelopment.kotelok.trainer.TrainerWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainWriteViewModel(
    dictionaryId: Long,
    cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModel() {
    private var dictId: Long = 0
    private val _viewState = MutableLiveData(TrainWriteFragment.State.NOT_GUESSED)
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    private val _isTrainerDone: MutableLiveData<Boolean> = MutableLiveData()
    val viewState: LiveData<TrainWriteFragment.State> = _viewState
    val trainerWriter: TrainerWriter = TrainerWriter(cardsLearnDefRepository, changeStatisticsRepository)
    val currentWord: LiveData<LearnableDefinition> = _currentWord
    val isTrainerDone: LiveData<Boolean>
        get() = _isTrainerDone

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            trainerWriter.loadDictionary(dictionaryId, onlyNotLearned = false)
//            val learnableDefinition = trainerWriter.getNext()
//            _currentWord.postValue(learnableDefinition)
            val isDone = trainerWriter.isDone
            _isTrainerDone.postValue(isDone)
            if (!isDone) {
                _currentWord.postValue(trainerWriter.getNext())
            }
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
//        if (!trainerWriter.isDone) {
//            _currentWord.value = trainerWriter.getNext()
//        }
        val isDone = trainerWriter.isDone
        _isTrainerDone.postValue(isDone)
        if (!isDone) {
            _currentWord.value = trainerWriter.getNext()
        }
    }

    fun onGuess(guess: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res: Boolean = trainerWriter.checkUserInput(guess)
            _viewState.postValue(
                if (res) TrainWriteFragment.State.GUESSED_CORRECT
                else TrainWriteFragment.State.GUESSED_INCORRECT
            )
        }
    }
}
