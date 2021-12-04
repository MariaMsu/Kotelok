package com.designdrivendevelopment.kotelok.screens.bottomNavigation

import androidx.fragment.app.Fragment

data class Tab(
    val name: String,
    private val hostFragmentInstantiation: () -> Fragment
) {
    val hostFragment: Fragment
        get() = hostFragmentInstantiation.invoke()
    val tag = "OPEN_" + name.uppercase()
}
