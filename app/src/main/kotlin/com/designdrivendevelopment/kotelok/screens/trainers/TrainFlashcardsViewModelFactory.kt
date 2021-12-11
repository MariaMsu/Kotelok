package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository

class TrainFlashcardsViewModelFactory(
    private val dictionaryId: Long,
    private val cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    private val changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainFlashcardsViewModel::class.java)) {
            return TrainFlashcardsViewModel(dictionaryId, cardsLearnDefRepository, changeStatisticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
