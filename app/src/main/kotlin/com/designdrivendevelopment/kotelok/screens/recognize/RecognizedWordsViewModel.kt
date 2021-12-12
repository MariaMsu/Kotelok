package com.designdrivendevelopment.kotelok.screens.recognize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecognizedWordsViewModel : ViewModel() {
    private val _recognizedWords: MutableLiveData<List<Word>> = MutableLiveData()
    val recognizedWords: LiveData<List<Word>>
        get() = _recognizedWords

    fun breakTextIntoWords(text: String) {
        val filteredText = text.filterNot { char -> char in SERVICE_SYMBOLS }
        val words = filteredText.split(" ", "\n").map {
            Word(writing = it.trim().lowercase())
        }.filterNot { it.writing == "" }
        _recognizedWords.value = words
    }

    companion object {
        private const val SERVICE_SYMBOLS = "!@\"№#;$%^:&?*()-=_+/\\|.,'<>`~[]{}"
    }
}
