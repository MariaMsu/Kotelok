package com.designdrivendevelopment.kotelok.screens.bottomNavigation

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import java.util.LinkedList

class BottomNavigator(
    private val tabs: List<Tab>,
    val fragmentContainer: Int
) {
    var currentTab = tabs.first()
        private set
    var fragmentManager: FragmentManager? = null
        private set
    private val tabsMap = tabs.map { tab -> tab.name to tab }.toMap()
    private val operationsQueue = LinkedList<String?>()
    private val onBackStackChangedListener by lazy { createOnBackStackChangedListener() }

    fun getTabByName(name: String) = tabsMap.getValue(name)

    private fun createOnBackStackChangedListener(): FragmentManager.OnBackStackChangedListener {
        return object : FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                Log.d("FRAGMENTS", "backStack is changed")
                if (fragmentManager == null) return
                Log.d("FRAGMENTS", "count = ${fragmentManager?.backStackEntryCount}")
                val operation = operationsQueue.poll()
                Log.d("FRAGMENTS", "operation = $operation")
                if (operation != OPERATION_OPEN_NEW_TAB) return
                for (tab in tabs) {
                    if (tab.name == currentTab.name) {
                        if (fragmentManager?.findFragmentByTag(tab.tag) == null) {
                            fragmentManager?.commit {
                                replace(fragmentContainer, tab.hostFragment, tab.tag)
                                addToBackStack(tab.name)
                                setReorderingAllowed(true)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onBackPressed() {
        if (fragmentManager?.backStackEntryCount == 1) {
            fragmentManager?.saveBackStack(currentTab.name)
            unsubscribe()
        }
        return
    }

    fun subscribe(fm: FragmentManager) {
        fragmentManager = fm
        fragmentManager?.addOnBackStackChangedListener(onBackStackChangedListener)
    }

    fun setDefaultTab(defaultTab: Tab) {
        currentTab = defaultTab
        operationsQueue.add(OPERATION_OPEN_NEW_TAB)
        fragmentManager?.commit {
            replace(fragmentContainer, defaultTab.hostFragment, defaultTab.tag)
            addToBackStack(defaultTab.name)
            setReorderingAllowed(true)
        }
    }

    fun unsubscribe() {
        fragmentManager?.removeOnBackStackChangedListener(onBackStackChangedListener)
        fragmentManager = null
    }

    fun selectTab(newTab: Tab) {
        Log.d("FRAGMENTS", "select ${newTab.name}")
        if (newTab.name == currentTab.name) return
        fragmentManager?.saveBackStack(currentTab.name)
        currentTab = newTab
        operationsQueue.add(OPERATION_OPEN_NEW_TAB)
        fragmentManager?.restoreBackStack(newTab.name)
    }

    companion object {
        const val OPERATION_OPEN_NEW_TAB = "OPERATION_OPEN_NEW_TAB"
    }
}
