package com.designdrivendevelopment.kotelok.entities

sealed class PartOfSpeech(
    val language: Language,
    val originalTitle: String,
    val russianTitle: String
) {
    class Noun(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, NOUN_RU_TITLE)

    class Verb(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, VERB_RU_TITLE)

    class Adjective(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, ADJECTIVE_RU_TITLE)

    class Pronoun(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, PRONOUN_RU_TITLE)

    class Numeral(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, NUMERAL_RU_TITLE)

    class Adverb(
        language: Language = Language.ENG,
        originalTitle: String
    ) : PartOfSpeech(language, originalTitle, ADVERB_RU_TITLE)

    class Other(
        language: Language = Language.ENG,
        originalTitle: String,
        russianTitle: String
    ) : PartOfSpeech(language, originalTitle, russianTitle)

    companion object {
        const val NOUN_RU_TITLE = "сущ."
        const val VERB_RU_TITLE = "гл."
        const val ADJECTIVE_RU_TITLE = "прил."
        const val PRONOUN_RU_TITLE = "мест."
        const val NUMERAL_RU_TITLE = "числ."
        const val ADVERB_RU_TITLE = "нар."
    }
}
