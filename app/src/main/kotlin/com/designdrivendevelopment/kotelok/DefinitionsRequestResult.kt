package com.designdrivendevelopment.kotelok

import com.designdrivendevelopment.kotelok.entities.WordDefinition

sealed class DefinitionsRequestResult {
    class Success(val definitions: List<WordDefinition>) : DefinitionsRequestResult()

    class EmptyResult : DefinitionsRequestResult()

    sealed class Failure(val errorMessage: String) : DefinitionsRequestResult() {
        class HttpError(val code: Int, message: String) : DefinitionsRequestResult.Failure(message)

        class Error(message: String) : DefinitionsRequestResult.Failure(message)
    }
}
