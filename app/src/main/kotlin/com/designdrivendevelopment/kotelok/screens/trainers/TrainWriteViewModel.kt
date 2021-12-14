package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.WriterLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository
import com.designdrivendevelopment.kotelok.trainer.TrainerWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainWriteViewModel(
    dictionaryId: Long,
    writerLearnDefRepository: WriterLearnableDefinitionsRepository,
    changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModel() {
    private var dictId: Long = Dictionary.NEW_DICTIONARY_ID
    private val _viewState = MutableLiveData<TrainWriteFragment.State>()
    private val _currentWord: MutableLiveData<LearnableDefinition> = MutableLiveData()
    private val trainerWriter: TrainerWriter = TrainerWriter(writerLearnDefRepository, changeStatisticsRepository)
    val viewState: LiveData<TrainWriteFragment.State> = _viewState
    val currentWord: LiveData<LearnableDefinition> = _currentWord

    init {
        dictId = dictionaryId
        viewModelScope.launch(Dispatchers.IO) {
            trainerWriter.loadDictionary(dictionaryId, onlyNotLearned = false)
            val isDone = trainerWriter.isDone
            if (isDone) {
                _viewState.postValue(TrainWriteFragment.State.DONE)
            } else {
                _currentWord.postValue(trainerWriter.getNext())
                _viewState.postValue(TrainWriteFragment.State.NOT_GUESSED)
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
        val isDone = trainerWriter.isDone
        if (isDone) {
            _viewState.value = TrainWriteFragment.State.DONE
        } else {
            _currentWord.value = trainerWriter.getNext()
            _viewState.value = TrainWriteFragment.State.NOT_GUESSED
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
