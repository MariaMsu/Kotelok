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
    private val _selectionStates = MutableLiveData(false)
    private val _selectionSize = MutableLiveData(0)
    private var currentItems: List<ItemWithType> = emptyList()
    private val selectedDefinitions: MutableList<WordDefinition> = mutableListOf()
    private var isSelectionActive: Boolean = false
        set(value) {
            if (value != field) {
                _selectionStates.value = value
            }
            field = value
        }
    val foundDefinitions: LiveData<List<ItemWithType>> = _foundDefinitions
    val events: LiveData<UiEvent<Any?>> = _events
    val selectionStates: LiveData<Boolean> = _selectionStates
    val selectionSize: LiveData<Int> = _selectionSize

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

    fun onSelectionCleared() {
        isSelectionActive = false
        currentItems = currentItems.map { item ->
            if (item is WordDefinitionItem) {
                item.copy(isSelected = false, isPartOfSelection = false)
            } else {
                item
            }
        }
        _foundDefinitions.value = currentItems
    }

    fun onSelectionSizeChanged(size: Int) {
        _selectionSize.value = size
    }

    fun onItemSelectionChanged(itemKey: String, selected: Boolean) {
        fun handleWhenSelectionActive(
            currentItems: List<ItemWithType>,
            selectedItem: WordDefinitionItem,
            selected: Boolean
        ): List<ItemWithType> {
            if (selected) {
                selectedDefinitions.add(selectedItem.data)
            } else {
                selectedDefinitions.remove(selectedItem.data)
                if (selectedDefinitions.isEmpty()) {
                    isSelectionActive = false
                    return setPartOfSelectionForList(currentItems, false)
                }
            }
            return currentItems
        }

        val selectedItem = currentItems.find { item -> item.stringKey == itemKey }
        when (selectedItem) {
            is WordDefinitionItem -> {
                if (selectedItem.isSelected == selected) {
                    return
                }
                currentItems = currentItems.toMutableList().apply {
                    set(
                        index = currentItems.indexOf(selectedItem),
                        element = selectedItem.copy(isSelected = selected)
                    )
                }.toList()

                if (isSelectionActive) {
                    currentItems = handleWhenSelectionActive(currentItems, selectedItem, selected)
                } else {
                    if (selected) {
                        selectedDefinitions.add(selectedItem.data)
                        isSelectionActive = true
                        currentItems = setPartOfSelectionForList(currentItems, true)
                    } else {
                        return
                    }
                }

                _foundDefinitions.value = currentItems
            }
        }
    }

    fun saveSelectedDefinitions() {

    }

    private fun setPartOfSelectionForList(
        items: List<ItemWithType>,
        isPartOfSelection: Boolean
    ): List<ItemWithType> {
        return items.map { item ->
            if (item is WordDefinitionItem) {
                item.copy(isPartOfSelection = isPartOfSelection)
            } else {
                item
            }
        }
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
