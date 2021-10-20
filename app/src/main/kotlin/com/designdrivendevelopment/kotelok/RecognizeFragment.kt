package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RecognizeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recognize, container, false)
    }

    companion object {
        const val OPEN_RECOGNIZE_TAG = "open_recognize"

        @JvmStatic
        fun newInstance() = RecognizeFragment()
    }
}
