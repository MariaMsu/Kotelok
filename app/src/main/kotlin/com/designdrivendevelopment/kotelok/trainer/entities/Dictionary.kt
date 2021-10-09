package com.designdrivendevelopment.kotelok.trainer.entities

// TODO как-то плохо мы назвали этот класс. Потому что в джаве есть свой дикшерари
// Словарь -\_(0_o)_/-
data class Dictionary(
    val label: String,
    val words: List<Word>,
    var isFavorite: Boolean
)
