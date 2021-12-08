package com.designdrivendevelopment.kotelok.screens.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.TotalStat
import com.designdrivendevelopment.kotelok.screens.profile.GetStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val getStatisticsRepository: GetStatisticsRepository,
) : ViewModel() {
    private val _totalStat = MutableLiveData<TotalStat>()
    val totalStat: LiveData<TotalStat> = _totalStat

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _totalStat.postValue(getStatisticsRepository.getStatisticsForAllDict())
        }
    }
}
