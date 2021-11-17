package com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen

data class UiEvent<out T : Any?>(
    val message: String,
    val data: T? = null,
    val isHandled: Boolean = false
)
