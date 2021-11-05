package com.designdrivendevelopment.kotelok.entities

data class Dictionary(
    val id: Long,
    val label: String,
    val wordCount: String,
    val wordDefinitions: List<WordDefinition>,
    var isFavorite: Boolean = false
)
