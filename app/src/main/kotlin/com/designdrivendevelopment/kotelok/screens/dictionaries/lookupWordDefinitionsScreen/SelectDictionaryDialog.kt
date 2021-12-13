package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Для диалога необходимо использовать массивы примитивов
@Suppress("ArrayPrimitive")
class SelectDictionaryDialog : DialogFragment() {
    private var ids: Array<Long> = emptyArray()
    private var labels: Array<String> = emptyArray()
    private val chosenDictionariesIds = mutableListOf<Long>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ids = arguments?.getLongArray(IDS_KEY)?.toTypedArray() ?: arrayOf()
        labels = arguments?.getStringArray(LABELS_KEY) ?: arrayOf()
        if (ids.isEmpty() or labels.isEmpty()) {
            dismiss()
        }
        val chosenDictionaries: BooleanArray = arguments?.getBooleanArray(SELECTED_KEY)
            ?: ids.map { false }.toBooleanArray()
        chosenDictionariesIds.addAll(ids.filterIndexed { index, _ -> chosenDictionaries[index] })

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите словарь")
            .setNeutralButton("Отмена") { _, _ -> dismiss() }
            .setPositiveButton("Сохранить") { _, _ ->
                val bundle = bundleOf(IDS_KEY to chosenDictionariesIds.toLongArray())
                setFragmentResult(FragmentResult.RecognizeTab.OPEN_SELECT_DICTIONARIES_KEY, bundle)
            }
            .setMultiChoiceItems(labels, chosenDictionaries) { _, which, checked ->
                if (checked) {
                    chosenDictionariesIds.add(ids[which])
                } else {
                    chosenDictionariesIds.remove(ids[which])
                }
            }.create()
    }

    companion object {
        const val IDS_KEY = "ids_key"
        const val LABELS_KEY = "labels_key"
        const val SELECTED_KEY = "selected_key"

        fun newInstance(
            dictIds: LongArray,
            dictLabels: Array<String>,
            selected: BooleanArray
        ): SelectDictionaryDialog {
            return SelectDictionaryDialog().apply {
                arguments = bundleOf(
                    IDS_KEY to dictIds,
                    LABELS_KEY to dictLabels,
                    SELECTED_KEY to selected
                )
            }
        }
    }
}
