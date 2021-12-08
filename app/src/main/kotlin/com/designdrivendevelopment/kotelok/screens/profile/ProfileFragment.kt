package com.designdrivendevelopment.kotelok.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult

class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResult(FragmentResult.ProfileTab.OPEN_STATISTICS_KEY, Bundle())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        const val OPEN_PROFILE_TAG = "open_profile"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
