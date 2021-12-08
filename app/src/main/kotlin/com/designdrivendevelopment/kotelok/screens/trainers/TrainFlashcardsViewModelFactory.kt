package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository

class TrainFlashcardsViewModelFactory(
    private val dictionaryId: Long,
    private val cardsLearnDefRepository: CardsLearnableDefinitionsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainFlashcardsViewModel::class.java)) {
            return TrainFlashcardsViewModel(dictionaryId, cardsLearnDefRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
