package com.designdrivendevelopment.kotelok.screens.trainers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.trainer.ChangeStatisticsRepository

class TrainWriteViewModelFactory(
    private val dictionaryId: Long,
    private val cardsLearnDefRepository: CardsLearnableDefinitionsRepository,
    private val changeStatisticsRepository: ChangeStatisticsRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TrainWriteViewModel::class.java)) {
            return TrainWriteViewModel(dictionaryId, cardsLearnDefRepository, changeStatisticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
