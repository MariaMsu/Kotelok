package com.designdrivendevelopment.kotelok.screens

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface SharedWordDefinitionProvider {
    var sharedWordDefinition: WordDefinition?
}
