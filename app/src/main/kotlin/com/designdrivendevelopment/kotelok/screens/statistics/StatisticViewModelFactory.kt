package com.designdrivendevelopment.kotelok.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.screens.profile.GetStatisticsRepository

class StatisticViewModelFactory(
    private val getStatisticsRepository: GetStatisticsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(StatisticViewModel::class.java)) {
            return StatisticViewModel(getStatisticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
