package com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes

abstract class CommonItem<out T : Any?> : ItemWithType {
    abstract val data: T
}
