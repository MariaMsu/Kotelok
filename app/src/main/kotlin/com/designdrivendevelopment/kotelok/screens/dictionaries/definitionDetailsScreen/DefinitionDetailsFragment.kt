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
        const val READ_ONLY = 1
        const val WRITE_AND_READ = 2
    }
}
