package com.designdrivendevelopment.kotelok.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.bottomNavigation.BottomNavigator
import com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen.DefinitionDetailsFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen.DictionaryDetailsFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsFragment
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.trainers.TrainWriteFragment
import com.designdrivendevelopment.kotelok.screens.statistics.StatisticFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private val bottomNavigator: BottomNavigator by lazy {
        (application as KotelokApplication).appComponent.bottomNavigator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bottomNavigator.subscribe(supportFragmentManager)
        setupDictionariesFragmentResultListeners()

        if (savedInstanceState == null) {
            val item = bottomNavigationView?.menu?.findItem(R.id.dictionary_tab)
            item?.isChecked = true
            bottomNavigator.setDefaultTab(bottomNavigator.getTabByName(DICTIONARIES_TAB))
        }
        addFragment(StatisticFragment())
    }

    override fun onDestroy() {
        bottomNavigator.unsubscribe()
        clearViews()
        super.onDestroy()
    }

    override fun onBackPressed() {
        bottomNavigator.onBackPressed()
        super.onBackPressed()
    }

    private fun setupDictionariesFragmentResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY,
                this@MainActivity
            ) { _, bundle ->
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(DictionaryDetailsFragment.RESULT_DATA_KEY)
                addFragment(
                    fragment = LookupWordDefinitionsFragment.newInstance(dictionaryId),
                    tag = "Lookup_word_def_fragment",
                    transactionName = "open_lookup_word_def_fragment"
                )
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_DEF_DETAILS_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(FragmentResult.DictionariesTab.RESULT_DICT_ID_KEY)
                val saveMode = bundle.getInt(FragmentResult.DictionariesTab.RESULT_SAVE_MODE_KEY)
                addFragment(
                    fragment = DefinitionDetailsFragment.newInstance(dictionaryId, saveMode),
                    tag = "def_details_fragment",
                    transactionName = "open_def_details_fragment"
                )
            }
        }
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView?.setOnItemSelectedListener { tab ->
            when (tab.itemId) {
                R.id.dictionary_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(DICTIONARIES_TAB))
                    true
                }
                R.id.recognition_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(RECOGNIZE_TAB))
                    true
                }
                R.id.profile_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(PROFILE_TAB))
                    true
                }
                else -> false
            }
        }
    }

    private fun clearViews() {
        bottomNavigationView = null
    }

    private fun addFragment(
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

    private fun replaceFragment(
        fragment: Fragment,
        tag: String? = null,
        transactionName: String? = null
    ) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment, tag)
            addToBackStack(transactionName)
            setReorderingAllowed(true)
        }
    }

    companion object {
        private const val DICTIONARIES_TAB = "DICTIONARIES"
        private const val RECOGNIZE_TAB = "RECOGNIZE"
        private const val PROFILE_TAB = "PROFILE"
    }
}
