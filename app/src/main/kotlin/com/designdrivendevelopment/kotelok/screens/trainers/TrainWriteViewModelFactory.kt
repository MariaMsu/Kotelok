package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository

class TrainWriteViewModelFactory(
    private val dictionaryId: Long,
    private val cardsLearnDefRepository: CardsLearnableDefinitionsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainWriteViewModel::class.java)) {
            return TrainWriteViewModel(dictionaryId, cardsLearnDefRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
