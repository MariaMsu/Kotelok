package com.designdrivendevelopment.kotelok.screens.screensUtils

sealed class UiEvent(var isHandled: Boolean) {
    class ShowMessage(val message: String, isHandled: Boolean = false) : UiEvent(isHandled)

    sealed class Loading(isHandled: Boolean) : UiEvent(isHandled) {
        class ShowLoading(isHandled: Boolean = false) : Loading(isHandled)

        class HideLoading(isHandled: Boolean = false) : Loading(isHandled)
    }
}
