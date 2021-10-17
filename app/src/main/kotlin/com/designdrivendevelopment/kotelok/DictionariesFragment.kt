package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class DictionariesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionaries, container, false)
    }

    companion object {
        const val OPEN_DICTIONARIES_TAG = "open_dictionaries"

        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }
}
