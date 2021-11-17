package com.designdrivendevelopment.kotelok

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
