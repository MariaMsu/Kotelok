package com.designdrivendevelopment.kotelok.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.TotalStat
import com.designdrivendevelopment.kotelok.screens.profile.GetStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatisticViewModel(
    getStatisticsRepository: GetStatisticsRepository,
) : ViewModel() {
    var totalStat: TotalStat? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            totalStat = getStatisticsRepository.getStatisticsForAllDict()
        }
    }
}
