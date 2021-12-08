package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResult
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrainersBottomSheet : BottomSheetDialogFragment() {
    private var cardsTrainerText: TextView? = null
    private var writerTrainerText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_trainers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun setupListeners() {
        cardsTrainerText?.setOnClickListener {
            setFragmentResult(FragmentResult.DictionariesTab.OPEN_CARDS_TRAINER_KEY, Bundle())
            dismiss()
        }
        writerTrainerText?.setOnClickListener {
            setFragmentResult(FragmentResult.DictionariesTab.OPEN_WRITER_TRAINER_KEY, Bundle())
            dismiss()
        }
    }

    private fun initViews(view: View) {
        cardsTrainerText = view.findViewById(R.id.cards_trainer_text)
        writerTrainerText = view.findViewById(R.id.writer_trainer_text)
    }

    private fun clearViews() {
        cardsTrainerText = null
        writerTrainerText = null
    }
}
