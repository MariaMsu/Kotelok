package com.designdrivendevelopment.kotelok.entities

data class Dictionary(
    val id: Long,
    val label: String,
    val size: Int,
    var isFavorite: Boolean
)
