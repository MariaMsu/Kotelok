package com.designdrivendevelopment.kotelok.trainer.utils

enum class StrChange {
    KEEP, INSERT, REPLACE, DELETE
}
typealias WordChangeArray = Array<Pair<Char, StrChange>>
