package com.designdrivendevelopment.kotelok

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _currentTabValue = MutableLiveData<String>(MainActivity.DICTIONARIES_TAB)
    val currentTabValue: LiveData<String> = _currentTabValue

    fun setNewTab(tab: String) {
        _currentTabValue.value = tab
    }
}
