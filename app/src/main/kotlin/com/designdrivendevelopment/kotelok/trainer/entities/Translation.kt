package com.designdrivendevelopment.kotelok.trainer.entities

// Вариант перевода
data class Translation(
    val id: Long,
    val language: Language = Language.ENG,
    val description: List<String>,
    val transcription: String,
    val examples: List<String>,
    var learntIndex: Float,
) {
    // learntIndex and other minor fields don't affect the essence of class instance
    override fun equals(other: Any?) =
        (other is Translation)
            && id == other.id
            && language == other.language
            && transcription == other.transcription

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + transcription.hashCode()
        return result
    }
}
