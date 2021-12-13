package com.designdrivendevelopment.kotelok.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileViewModelFactory(
    private val statisticsRepository: GetStatisticsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(statisticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
