package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout

class TrainWriteFragment : Fragment() {
    private var inputText : TextInputLayout? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(dictionaryId: Long) = TrainWriteFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED
    }

}
