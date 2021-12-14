package com.designdrivendevelopment.kotelok.application

import android.content.Context
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.persistence.database.KotelokDatabase
import com.designdrivendevelopment.kotelok.repositoryImplementations.changeStatisticsRepository.ChangeStatisticsRepositoryImpl
import com.designdrivendevelopment.kotelok.repositoryImplementations.dictionariesRepository.DictionariesRepositoryImpl
import com.designdrivendevelopment.kotelok.repositoryImplementations.dictionaryWordDefinitionsRepository.DictWordDefinitionRepositoryImpl
import com.designdrivendevelopment.kotelok.repositoryImplementations.editWordDefnititionsRepository.EditWordDefRepositoryImpl
import com.designdrivendevelopment.kotelok.repositoryImplementations.getStatisticsRepository.GetStatisticsRepositoryImpl
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.CardsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.PairsLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.repositoryImplementations.learnableDefinitionsRepository.WriterLearnableDefinitionsRepository
import com.designdrivendevelopment.kotelok.repositoryImplementations.lookupWordDefinitionRepository.LookupWordDefRepositoryImpl
import com.designdrivendevelopment.kotelok.screens.bottomNavigation.BottomNavigator
import com.designdrivendevelopment.kotelok.screens.bottomNavigation.Tab
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen.DictionariesFragment
import com.designdrivendevelopment.kotelok.screens.profile.ProfileFragment
import com.designdrivendevelopment.kotelok.screens.recognize.cameraScreen.RecognizeFragment
import com.designdrivendevelopment.kotelok.screens.sharedWordDefProvider.SharedWordDefProviderImpl
import com.designdrivendevelopment.kotelok.yandexDictApi.RetrofitModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AppComponent(applicationContext: Context) {
    val bottomNavigator by lazy {
        BottomNavigator(
            listOf(
                Tab("DICTIONARIES") { DictionariesFragment.newInstance() },
                Tab("RECOGNIZE") { RecognizeFragment.newInstance() },
                Tab("PROFILE") { ProfileFragment.newInstance() },
            ),
            R.id.fragment_container
        )
    }

    private val yandexDictionaryApiService = RetrofitModule().yandexDictionaryService
    private val db = KotelokDatabase.create(applicationContext, CoroutineScope(Dispatchers.IO))
    val dictionariesRepository by lazy {
        DictionariesRepositoryImpl(
            db.dictionariesDao,
            db.dictionaryWordDefCrossRefDao
        )
    }
    val dictDefinitionsRepository by lazy {
        DictWordDefinitionRepositoryImpl(db.wordDefinitionsDao, db.dictionaryWordDefCrossRefDao)
    }
    val cardsLearnDefRepository by lazy {
        CardsLearnableDefinitionsRepository(db.cardsLearnableDefDao)
    }
    val writerLearnDefRepository by lazy {
        WriterLearnableDefinitionsRepository(db.writerLearnableDefDao)
    }
    val pairsLearnDefRepository by lazy {
        PairsLearnableDefinitionsRepository(db.pairsLearnableDefDao)
    }
    val changeStatisticsRepositoryImpl by lazy {
        ChangeStatisticsRepositoryImpl(db.statisticsDao)
    }
    val statisticsRepository by lazy { GetStatisticsRepositoryImpl(db.statisticsDao) }
    val lookupWordDefRepository by lazy {
        LookupWordDefRepositoryImpl(
            yandexDictApiService = yandexDictionaryApiService,
            wordDefinitionsDao = db.wordDefinitionsDao
        )
    }
    val editWordDefinitionsRepository by lazy {
        EditWordDefRepositoryImpl(
            wordDefinitionsDao = db.wordDefinitionsDao,
            dictWordDefCrossRefDao = db.dictionaryWordDefCrossRefDao,
            translationsDao = db.translationsDao,
            synonymsDao = db.synonymsDao,
            examplesDao = db.examplesDao
        )
    }
    val sharedWordDefProvider by lazy { SharedWordDefProviderImpl() }
}
