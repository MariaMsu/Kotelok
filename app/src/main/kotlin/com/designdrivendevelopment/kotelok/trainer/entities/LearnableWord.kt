package com.designdrivendevelopment.kotelok.trainer.entities

//Приходит на вход тренажера
data class LearnableWord(
    val parentWordId: Long,
    val writing: String,
    val translation: Translation
)
