package com.designdrivendevelopment.kotelok.trainer.entities

// Вылезает из базы данных
data class Word(
    val id: Long,
    val writing: String,
    val translations: List<Translation>
)
