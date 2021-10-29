package com.designdrivendevelopment.kotelok

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null

    private val viewModel: MainViewModel by viewModels()
    private var currentTab: String = DICTIONARIES_TAB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "grhfkngkrekg"
        initViews()
        setupViewModel()
        openFragment(
            fragment = DictionariesFragment.newInstance(),
            tag = DictionariesFragment.OPEN_DICTIONARIES_TAG,
            transactionName = DICTIONARIES_TAB
        )
        setupOnBackStackListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearViews()
    }

    private fun setupOnBackStackListener() {
        val fm = supportFragmentManager
        fm.addOnBackStackChangedListener {
            when (currentTab) {
                DICTIONARIES_TAB -> {
                    if (fm.findFragmentByTag(DictionariesFragment.OPEN_DICTIONARIES_TAG) == null) {
                        openFragment(
                            fragment = DictionariesFragment.newInstance(),
                            tag = DictionariesFragment.OPEN_DICTIONARIES_TAG,
                            DICTIONARIES_TAB
                        )
                    }
                }
                RECOGNIZE_TAB -> {
                    if (fm.findFragmentByTag(RecognizeFragment.OPEN_RECOGNIZE_TAG) == null) {
                        openFragment(
                            fragment = RecognizeFragment.newInstance(),
                            tag = RecognizeFragment.OPEN_RECOGNIZE_TAG,
                            transactionName = RECOGNIZE_TAB
                        )
                    }
                }
                PROFILE_TAB -> {
                    if (fm.findFragmentByTag(ProfileFragment.OPEN_PROFILE_TAG) == null) {
                        openFragment(
                            fragment = ProfileFragment.newInstance(),
                            tag = ProfileFragment.OPEN_PROFILE_TAG,
                            transactionName = PROFILE_TAB
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun setupViewModel() {
        viewModel.currentTabValue.observe(this) { newTab ->
            currentTab = newTab
        }
    }

    private fun setupDictionariesFragmentResultListeners() {
        supportFragmentManager.setFragmentResultListener(
            FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY,
            this@MainActivity
        ) { _, bundle ->
        }
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView?.setOnItemSelectedListener { tab ->
            when (tab.itemId) {
                R.id.dictionary_tab -> {
                    selectTab(currentTab, DICTIONARIES_TAB)
                    true
                }
                R.id.recognition_tab -> {
                    selectTab(currentTab, RECOGNIZE_TAB)
                    true
                }
                R.id.profile_tab -> {
                    selectTab(currentTab, PROFILE_TAB)
                    true
                }
                else -> false
            }
        }
    }

    private fun clearViews() {
        bottomNavigationView = null
    }

    private fun selectTab(currentTab: String, newTab: String) {
        if (newTab == currentTab) return
        supportFragmentManager.saveBackStack(currentTab)
        viewModel.setNewTab(newTab)
        supportFragmentManager.restoreBackStack(newTab)
    }

    private fun openFragment(
        fragment: Fragment,
        tag: String? = null,
        transactionName: String? = null
    ) {
        supportFragmentManager.commit {
            add(R.id.fragment_container, fragment, tag)
            addToBackStack(transactionName)
            setReorderingAllowed(true)
        }
    }

    companion object {
        const val DICTIONARIES_TAB = "dictionaries_tab"
        const val RECOGNIZE_TAB = "recognize_tab"
        const val PROFILE_TAB = "profile_tab"
    }
}
