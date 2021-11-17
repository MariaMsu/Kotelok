package com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.designdrivendevelopment.kotelok.R

class LookupWordDefinitionsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_lookup_word_definition,
            container,
            false
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = LookupWordDefinitionsFragment()
    }
}
