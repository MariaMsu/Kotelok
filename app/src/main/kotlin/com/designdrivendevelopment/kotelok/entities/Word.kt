package com.designdrivendevelopment.kotelok.entities

data class Word(
    val id: Long,
    val language: Language,
    val writing: String,
    val definitions: List<WordDefinition>
)
