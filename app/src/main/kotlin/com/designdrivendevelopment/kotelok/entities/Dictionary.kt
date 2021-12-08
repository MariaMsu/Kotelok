package com.designdrivendevelopment.kotelok.entities

data class Dictionary(
    val id: Long,
    val label: String,
    val size: Int,
    var isFavorite: Boolean
) {
    companion object {
        const val NEW_DICTIONARY_ID = 0L
        const val SIZE_EMPTY = 0
    }
}
