package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DictionariesFragment : Fragment() {
    private var dictionariesList: RecyclerView? = null
    private var addDictionaryFab: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionaries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun initViews(view: View) {
        dictionariesList = view.findViewById(R.id.dictionaries_list)
        addDictionaryFab = view.findViewById(R.id.add_dictionary_button)
    }

    private fun clearViews() {
        dictionariesList = null
        addDictionaryFab = null
    }

    companion object {
        const val OPEN_DICTIONARIES_TAG = "open_dictionaries"

        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }
}
