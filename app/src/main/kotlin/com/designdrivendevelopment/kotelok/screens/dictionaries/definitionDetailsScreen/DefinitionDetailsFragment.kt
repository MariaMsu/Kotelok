package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.designdrivendevelopment.kotelok.R

class DefinitionDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_definition_details, container, false)
    }

    companion object {
        private const val DICT_ID_KEY = "dictionary_id_key"
        private const val SAVE_MODE_KEY = "save_mode_key"
        const val READ_ONLY = 1
        const val WRITE_AND_READ = 2
        const val SAVE_MODE_UPDATE = 1
        const val SAVE_MODE_COPY = 2

        fun newInstance(
            dictionaryId: Long,
            saveMode: Int
        ) = DefinitionDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(DICT_ID_KEY, dictionaryId)
                putInt(SAVE_MODE_KEY, saveMode)
            }
        }
    }
}
