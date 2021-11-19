package com.designdrivendevelopment.kotelok.screens.screensUtils

data class UiEvent<out T : Any?>(
    val message: String,
    val data: T? = null,
    val isHandled: Boolean = false
)
