package com.designdrivendevelopment.kotelok.persistence.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.Language
import com.designdrivendevelopment.kotelok.entities.PartOfSpeech
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.persistence.converters.DateConverter
import com.designdrivendevelopment.kotelok.persistence.daos.DictionariesDao
import com.designdrivendevelopment.kotelok.persistence.daos.DictionaryWordDefCrossRefDao
import com.designdrivendevelopment.kotelok.persistence.daos.ExamplesDao
import com.designdrivendevelopment.kotelok.persistence.daos.PartsOfSpeechDao
import com.designdrivendevelopment.kotelok.persistence.daos.StatisticsDao
import com.designdrivendevelopment.kotelok.persistence.daos.SynonymsDao
import com.designdrivendevelopment.kotelok.persistence.daos.TranslationsDao
import com.designdrivendevelopment.kotelok.persistence.daos.WordDefinitionsDao
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.DictionaryWordDefCrossRef
import com.designdrivendevelopment.kotelok.persistence.roomEntities.ExampleEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.PartOfSpeechEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.SynonymEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.TranslationEntity
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

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
    abstract val partsOfSpeechDao: PartsOfSpeechDao
    abstract val synonymsDao: SynonymsDao
    abstract val translationsDao: TranslationsDao
    abstract val wordDefinitionsDao: WordDefinitionsDao
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
                .addCallback(PrepopulateCallback(coroutineScope))
                .build()

            database = instance
            Log.d("DATABASE", "database exists is ${database != null}")
            return instance
        }

        private class PrepopulateCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            private fun getDefinitions(): List<WordDefinition> {
                val definitions = listOf(
                    WordDefinition(
                        id = 0,
                        writing = "time",
                        partOfSpeech = PartOfSpeech.Noun(
                            language = Language.ENG,
                            originalTitle = "noun"
                        ),
                        transcription = "time",
                        synonyms = listOf("period", "once", "moment"),
                        mainTranslation = "время",
                        otherTranslations = listOf("раз", "момент", "срок"),
                        examples = listOf(
                            ExampleOfDefinitionUse(
                                wordDefinitionId = 0,
                                originalText = "take some time",
                                translatedText = "занять некоторое время"
                            )
                        ),
                        nextRepeatDate = with(Calendar.getInstance()) {
                            add(Calendar.DAY_OF_MONTH, 5)
                            time
                        }
                    ),
                    WordDefinition(
                        id = 0,
                        writing = "time",
                        partOfSpeech = PartOfSpeech.Noun(
                            language = Language.ENG,
                            originalTitle = "noun"
                        ),
                        transcription = "time",
                        synonyms = listOf("hour"),
                        mainTranslation = "час",
                        otherTranslations = emptyList(),
                        examples = listOf(
                            ExampleOfDefinitionUse(
                                wordDefinitionId = 0,
                                originalText = "checkout time",
                                translatedText = "расчетный час"
                            )
                        ),
                        nextRepeatDate = with(Calendar.getInstance()) {
                            add(Calendar.DAY_OF_MONTH, 3)
                            time
                        }
                    )
                )

                return definitions
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch(Dispatchers.IO) {
                    val wordDefinitionsDao = database?.wordDefinitionsDao
                    val translationsDao = database?.translationsDao
                    val partsOfSpeechDao = database?.partsOfSpeechDao
                    val synonymsDao = database?.synonymsDao
                    val examplesDao = database?.examplesDao
                    val dictionariesDao = database?.dictionariesDao
                    val dictionaryWordDefCrossRefDao = database?.dictionaryWordDefCrossRefDao

                    val dictionaryId = dictionariesDao!!.insert(
                        DictionaryEntity(
                            id = 0,
                            label = "Time",
                            isFavorite = false
                        )
                    )

                    val defs = getDefinitions()
                    defs.forEach { def ->
                        val entity = WordDefinitionEntity(
                            id = 0,
                            writing = "time",
                            language = Language.ENG,
                            partOfSpeech = if (def.synonyms.first() == "hour") null
                            else def.partOfSpeech?.originalTitle,
                            transcription = def.transcription,
                            mainTranslation = def.mainTranslation,
                            nextRepeatDate = def.nextRepeatDate,
                            repetitionNumber = 2,
                            interval = 5,
                            easinessFactor = 2.5F
                        )
                        val wordDefId = wordDefinitionsDao!!.insert(entity)

                        val translations = def.otherTranslations + def.mainTranslation
                        translations.forEach { tr ->
                            translationsDao?.insert(
                                TranslationEntity(
                                    wordDefinitionId = wordDefId,
                                    translation = tr
                                )
                            )
                        }

                        if (def.partOfSpeech != null) {
                            partsOfSpeechDao?.insert(
                                PartOfSpeechEntity(
                                    language = def.partOfSpeech.language,
                                    originalTitle = def.partOfSpeech.originalTitle,
                                    russianTitle = def.partOfSpeech.russianTitle
                                )
                            )
                        }

                        def.synonyms.forEach { synonym ->
                            synonymsDao?.insert(
                                SynonymEntity(
                                    wordDefinitionId = wordDefId,
                                    writing = synonym
                                )
                            )
                        }

                        def.examples.forEach { example ->
                            examplesDao?.insert(
                                ExampleEntity(
                                    wordDefinitionId = wordDefId,
                                    original = example.originalText,
                                    translation = example.translatedText
                                )
                            )
                        }

                        dictionaryWordDefCrossRefDao?.insert(
                            DictionaryWordDefCrossRef(
                                dictionaryId,
                                wordDefId
                            )
                        )
                    }
                }
            }
        }
    }
}
