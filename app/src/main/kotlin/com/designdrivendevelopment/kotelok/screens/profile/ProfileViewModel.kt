package com.designdrivendevelopment.kotelok.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.DictionaryStatistic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val statisticsRepository: GetStatisticsRepository
) : ViewModel() {
    private val _answersValues: MutableLiveData<List<Float>> = MutableLiveData()
    private val _dictionariesStat: MutableLiveData<List<DictionaryStatistic>> = MutableLiveData()
    val answersValues: LiveData<List<Float>>
        get() = _answersValues
    val dictionariesStat: LiveData<List<DictionaryStatistic>>
        get() = _dictionariesStat

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                statisticsRepository.getAnswersStatistic().collect { answerStat ->
                    val values = listOf(
                        (answerStat.totalAnswers - answerStat.successfullyAnswers).toFloat(),
                        answerStat.successfullyAnswers.toFloat(),
                        answerStat.totalAnswers.toFloat()
                    )
                    _answersValues.postValue(values)
                }
            }
            launch {
                statisticsRepository.getStatisticsForAllDict().collect { dictionariesStat ->
                    _dictionariesStat.postValue(dictionariesStat)
                }
            }
        }
    }
}
