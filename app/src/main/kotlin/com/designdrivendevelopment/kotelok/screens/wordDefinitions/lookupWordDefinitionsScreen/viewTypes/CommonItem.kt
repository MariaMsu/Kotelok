package com.designdrivendevelopment.kotelok.screens.wordDefinitions.lookupWordDefinitionsScreen.viewTypes

abstract class CommonItem<out T : Any?> : ItemWithType {
    abstract val data: T
}
