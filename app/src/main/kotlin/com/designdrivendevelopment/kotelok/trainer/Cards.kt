package com.designdrivendevelopment.kotelok.trainer

import com.designdrivendevelopment.kotelok.trainer.entities.LearnableWord
import java.lang.RuntimeException

class Cards(private val learnableWords: List<LearnableWord>) {
    private var currentIdx = -1;  // TODO тут какой-то нулевой тип должен быть. Что будет означать, что слово не выдано

    public fun getNextWord(): LearnableWord {
        if (this.currentIdx == -1) {
            throw RuntimeException("Try to get learnable word before the result of the previous one is returned")
        }
        var index = 0; // TODO сделать выбиралку инрлексов
        this.currentIdx = index
        return learnableWords[index]
    }

    public fun setCurrentResult(isRight: Boolean) {
        if (this.currentIdx == -1) {
            throw RuntimeException("Try to set result to an unspecified learnable word")
        }
        learnableWords[this.currentIdx].translation.learntIndex += 0.1f
        this.currentIdx = -1
    }
}
