package com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes

class CommonItem<out T: Any>(
    val item: T,
    private val type: Int
): ItemWithType {
    override val viewType: Int
        get() = type
}
