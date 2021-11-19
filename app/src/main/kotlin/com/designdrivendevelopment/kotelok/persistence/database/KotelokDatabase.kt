package com.designdrivendevelopment.kotelok.persistence.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.designdrivendevelopment.kotelok.persistence.converters.DateConverter
import com.designdrivendevelopment.kotelok.persistence.daos.CardsLearnableDefDao
import com.designdrivendevelopment.kotelok.persistence.daos.DictionariesDao
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.ExamplesDao
import com.designdrivendevelopment.kotelok.persistence.daos.PairsLearnableDefDao
import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.persistence.daos.SynonymsDao
import com.designdrivendevelopment.kotelok.persistence.daos.TranslationsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WriterLearnableDefDao
import com.designdrivendevelopment.kotelok.persistence.prepopulating.AssetsRepository
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.getExampleEntities
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.getSynonymEntities
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.getTranslationEntities
import com.designdrivendevelopment.kotelok.repositoryImplementations.extensions.getWordDefinitionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DictionaryEntity::class,
        ExampleEntity::class,
        PartOfSpeechEntity::class,
        SynonymEntity::class,
        TranslationEntity::class,
        WordDefinitionEntity::class,
        DictionaryWordDefCrossRef::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class KotelokDatabase : RoomDatabase() {
    abstract val dictionariesDao: DictionariesDao
    abstract val examplesDao: ExamplesDao
    abstract val synonymsDao: SynonymsDao
    abstract val translationsDao: TranslationsDao
    abstract val wordDefinitionsDao: WordDefinitionsDao
    abstract val cardsLearnableDefDao: CardsLearnableDefDao
    abstract val writerLearnableDefDao: WriterLearnableDefDao
    abstract val pairsLearnableDefDao: PairsLearnableDefDao
    abstract val dictionaryWordDefCrossRefDao: DictionaryWordDefCrossRefDao
    abstract val statisticsDao: StatisticsDao

    companion object {
        private var database: KotelokDatabase? = null

        fun create(applicationContext: Context, coroutineScope: CoroutineScope): KotelokDatabase {
            val instance = Room.databaseBuilder(
                applicationContext,
                KotelokDatabase::class.java,
                "Kotelok_db"
            )
                .fallbackToDestructiveMigration()
                .addCallback(PrepopulateCallback(applicationContext, coroutineScope))
                .build()

            database = instance
            Log.d("DATABASE", "database exists is ${database != null}")
            return instance
        }

        private class PrepopulateCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            private fun prepopulate(context: Context, coroutineScope: CoroutineScope) {
                val wordDefinitionsDao = database?.wordDefinitionsDao
                val translationsDao = database?.translationsDao
                val synonymsDao = database?.synonymsDao
                val examplesDao = database?.examplesDao
                val dictionariesDao = database?.dictionariesDao
                val dictionaryWordDefCrossRefDao = database?.dictionaryWordDefCrossRefDao

                val assetsRepository = AssetsRepository(context)
                val definitions = assetsRepository.readDefinitionsFromAssets()
                coroutineScope.launch(Dispatchers.IO) {
                    val dictionaryId = dictionariesDao!!.insert(
                        DictionaryEntity(
                            id = 0,
                            label = "Базовый словарь",
                            isFavorite = false
                        )
                    )

                    definitions.forEach { definitionResponse ->
                        val writing = definitionResponse.writing
                        val transcription = definitionResponse.transcription

                        definitionResponse.translations.forEach { translationResponse ->
                            val wordDefinitionEntity = translationResponse
                                .getWordDefinitionEntity(writing, transcription)
                            val defId = wordDefinitionsDao!!.insert(wordDefinitionEntity)

                            val synonyms = translationResponse.getSynonymEntities(defId)
                            synonymsDao?.insert(synonyms)

                            val translations = translationResponse.getTranslationEntities(defId)
                            translationsDao?.insert(translations)

                            val examples = translationResponse.getExampleEntities(defId)
                            examplesDao?.insert(examples)

                            dictionaryWordDefCrossRefDao?.insert(
                                DictionaryWordDefCrossRef(
                                    dictionaryId = dictionaryId,
                                    wordDefinitionId = defId
                                )
                            )
                        }
                    }
                }
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                prepopulate(context, scope)
            }
        }
    }
}
