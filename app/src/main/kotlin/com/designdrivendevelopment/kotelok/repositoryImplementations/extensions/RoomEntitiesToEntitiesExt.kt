package com.designdrivendevelopment.kotelok.repositoryImplementations.extensions

import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity

fun DictionaryEntity.toDictionary(size: Int): Dictionary {
    return Dictionary(
        id = this.id,
        label = this.label,
        isFavorite = this.isFavorite,
        size = size
    )
}

fun ExampleEntity.toExampleOfDefinitionUse(): ExampleOfDefinitionUse {
    return ExampleOfDefinitionUse(
        originalText = this.original,
        translatedText = this.translation
    )
}
