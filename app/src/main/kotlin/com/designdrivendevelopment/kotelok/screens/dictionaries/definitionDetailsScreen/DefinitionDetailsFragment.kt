package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication

class DefinitionDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_definition_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dictionaryId = arguments?.getLong(DICT_ID_KEY) ?: DEFAULT_DICT_ID
        val saveMode = arguments?.getInt(SAVE_MODE_KEY) ?: SAVE_MODE_COPY

        val activity = requireActivity()
        val factory = DefDetailsViewModelFactory(
            saveMode,
            dictionaryId,
            (activity.application as KotelokApplication)
                .appComponent.editWordDefinitionsRepository,
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository,
            (activity.application as KotelokApplication)
                .appComponent.sharedWordDefProvider
        )
        val viewModel = ViewModelProvider(this, factory)[DefDetailsViewModel::class.java]
    }

    companion object {
        private const val DICT_ID_KEY = "dictionary_id_key"
        private const val SAVE_MODE_KEY = "save_mode_key"
        private const val DEFAULT_DICT_ID = 1L
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
