package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.stringKey
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem
import com.designdrivendevelopment.kotelok.screens.screensUtils.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LookupViewModel(
    private val lookupWordDefRepository: LookupWordDefinitionsRepository
) : ViewModel() {
    private val _foundDefinitions = MutableLiveData<List<ItemWithType>>(emptyList())
    private val _events = MutableLiveData<UiEvent<Any?>>()
    private var currentItems: List<ItemWithType> = emptyList()
    val foundDefinitions: LiveData<List<ItemWithType>> = _foundDefinitions
    val events: LiveData<UiEvent<Any?>> = _events

    fun lookupByWriting(writing: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val flowFromRemote = lookupWordDefRepository.loadDefinitionsByWriting(writing)
            val flowFromLocal = lookupWordDefRepository.getSavedDefinitionsByWriting(writing)

            flowFromRemote.combine(flowFromLocal) { networkResult, localDefinitions ->
                Pair(networkResult, localDefinitions)
            }.collect { pair ->
                val networkResult = pair.first
                val localDefinitions = pair.second
                val items: List<ItemWithType> = when (networkResult) {
                    is DefinitionsRequestResult.Failure.Error -> {
                        _events.postValue(
                            UiEvent(
                                message = "Обнаружены проблемы с интернет соединением." +
                                    "Попробуйте повторить попытку"
                            )
                        )
                        createItemsList(localDefinitions, emptyList())
                    }

                    is DefinitionsRequestResult.Failure.HttpError -> {
                        _events.postValue(
                            UiEvent(
                                message = "Упс, что-то пошло не так" +
                                    "Попробуйте повторить попытку"
                            )
                        )
                        createItemsList(localDefinitions, emptyList())
                    }

                    is DefinitionsRequestResult.Success -> {
                        val remoteDefinitions = networkResult.definitions
                        if (remoteDefinitions.isEmpty()) {
                            _events.postValue(
                                UiEvent(
                                    message = "В словаре не найдены определения для данного слова"
                                )
                            )
                        }
                        createItemsList(localDefinitions, remoteDefinitions)
                    }
                }
                currentItems = items
                _foundDefinitions.postValue(items)
            }
        }
    }

    fun notifyToEventIsHandled(event: UiEvent<*>) {
        _events.value = event.copy(isHandled = true)
    }

    fun onItemSelectionChanged(itemKey: String, selected: Boolean) {
        currentItems = currentItems.map { item ->
            if (item.stringKey == itemKey && item is WordDefinitionItem) {
                item.apply { isSelected = selected }
            } else {
                item
            }
        }
        _foundDefinitions.postValue(currentItems)
    }

    private fun createItemsList(
        localDefinitions: List<WordDefinition>,
        remoteDefinitions: List<WordDefinition>
    ): List<ItemWithType> {
        val items = mutableListOf<ItemWithType>()
        if (remoteDefinitions.isNotEmpty()) {
            items.add(CategoryHeaderItem(header = "Загруженные определения"))
            items.addAll(remoteDefinitions.map { WordDefinitionItem(it) })
        }
        if (localDefinitions.isNotEmpty()) {
            items.add(CategoryHeaderItem(header = "Сохраненные определения"))
            items.addAll(localDefinitions.map { WordDefinitionItem(it) })
        }
        return items.toList()
    }
}
