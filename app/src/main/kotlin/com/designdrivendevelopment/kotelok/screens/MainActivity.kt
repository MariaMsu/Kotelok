package com.designdrivendevelopment.kotelok.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.screens.bottomNavigation.BottomNavigator
import com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen.AddDictionaryFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen.DefinitionDetailsFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen.DictionariesFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen.TrainersBottomSheet
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen.DictionaryDetailsFragment
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsFragment
import com.designdrivendevelopment.kotelok.screens.recognize.RecognizedTextBottomSheet
import com.designdrivendevelopment.kotelok.screens.recognize.RecognizedWordsFragment
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.statistics.StatisticFragment
import com.designdrivendevelopment.kotelok.screens.trainers.TrainFlashcardsFragment
import com.designdrivendevelopment.kotelok.screens.trainers.TrainWriteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("TooManyFunctions")
class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private val bottomNavigator: BottomNavigator by lazy {
        (application as KotelokApplication).appComponent.bottomNavigator
    }
    private var trainedDictionaryId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bottomNavigator.subscribe(supportFragmentManager)
        setupDictionariesFragmentResultListeners()
        setupDefinitionsResultListeners()
        setupTrainersDialogResultListeners()
        setupProfileResultListeners()
        setupRecognizeResultListener()

        if (savedInstanceState == null) {
            val item = bottomNavigationView?.menu?.findItem(R.id.dictionary_tab)
            item?.isChecked = true
            bottomNavigator.setDefaultTab(bottomNavigator.getTabByName(DICTIONARIES_TAB))
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_CODE) {
            if (allPermissionsGranted(this)) {
                bottomNavigator.selectTab(bottomNavigator.getTabByName(RECOGNIZE_TAB))
                val item = bottomNavigationView?.menu?.findItem(R.id.recognition_tab)
                item?.isChecked = true
            }
        }
    }

    private fun setupDictionariesFragmentResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(DictionariesFragment.DICT_ID_KEY)
                val dictionaryLabel = bundle.getString(
                    DictionariesFragment.DICT_LABEL_KEY,
                    getString(R.string.app_name)
                )
                replaceFragment(
                    fragment = DictionaryDetailsFragment.newInstance(dictionaryId, dictionaryLabel),
                    tag = "dictionary_details_fragment"
                )
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_ADD_DICTIONARY_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(AddDictionaryFragment.newInstance(), "add_dictionary_fragment")
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_NEW_DICTIONARY_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(AddDictionaryFragment.DICT_ID_KEY)
                val label = bundle.getString(
                    AddDictionaryFragment.DICT_LABEL_KEY,
                    getString(R.string.app_name)
                )
                supportFragmentManager.commit {
                    replace(
                        R.id.fragment_container,
                        DictionaryDetailsFragment.newInstance(dictionaryId, label),
                        "new_dictionary_fragment"
                    )
                    setReorderingAllowed(true)
                }
            }
        }
    }

    private fun setupDefinitionsResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(
                    DictionaryDetailsFragment.RESULT_DATA_KEY,
                    Dictionary.DEFAULT_DICT_ID
                )
                val word = bundle.getString(LookupWordDefinitionsFragment.LOOKUP_WORD_KEY, "")
                replaceFragment(
                    fragment = LookupWordDefinitionsFragment.newInstance(dictionaryId, word),
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

    private fun setupTrainersDialogResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_TRAINERS_DIALOG_KEY,
                this@MainActivity
            ) { _, bundle ->
                trainedDictionaryId = bundle.getLong(DictionariesFragment.DICT_ID_KEY)
                val trainersBottomSheet = TrainersBottomSheet()
                trainersBottomSheet.show(supportFragmentManager, "trainers_bottom_sheet_tag")
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_CARDS_TRAINER_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(TrainFlashcardsFragment.newInstance(trainedDictionaryId ?: 1))
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_WRITER_TRAINER_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(TrainWriteFragment.newInstance(trainedDictionaryId ?: 1))
            }
        }
    }

    private fun setupProfileResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.ProfileTab.OPEN_STATISTICS_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(StatisticFragment())
            }
        }
    }

    private fun setupRecognizeResultListener() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.RecognizeTab.OPEN_RECOGNIZED_TEXT_DIALOG,
                this@MainActivity
            ) { _, bundle ->
                val text = bundle.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
                val recognizedTextBottomSheet = RecognizedTextBottomSheet.newInstance(text)
                recognizedTextBottomSheet.show(this, null)
            }
            setFragmentResultListener(
                FragmentResult.RecognizeTab.OPEN_RECOGNIZED_WORDS_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val text = bundle.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
                replaceFragment(RecognizedWordsFragment.newInstance(text), "recognized_words_fragment")
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
                    if (allPermissionsGranted(this)) {
                        bottomNavigator.selectTab(bottomNavigator.getTabByName(RECOGNIZE_TAB))
                        true
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            REQUIRED_PERMISSIONS,
                            PERMISSIONS_CODE
                        )
                        false
                    }
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

    private fun allPermissionsGranted(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val DICTIONARIES_TAB = "DICTIONARIES"
        private const val RECOGNIZE_TAB = "RECOGNIZE"
        private const val PROFILE_TAB = "PROFILE"
        private const val PERMISSIONS_CODE = 42
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
