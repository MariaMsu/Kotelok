package com.designdrivendevelopment.kotelok.screens.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.designdrivendevelopment.kotelok.R

class StatisticFragment : Fragment(){
    private var textView2 : TextView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView2 = view.findViewById(R.id.textView2)
        textView2?.text = "говно жопа"
    }
}
