package com.designdrivendevelopment.kotelok.screens.recognize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecognizeViewModel : ViewModel() {
    private val _recognizedText: MutableLiveData<String> = MutableLiveData()
    val recognizedText: LiveData<String>
        get() = _recognizedText

    fun onTextRecognized(text: String) {
        _recognizedText.value = text
    }
}
