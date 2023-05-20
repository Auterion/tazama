package com.auterion.tazama.survey

fun Int.WrapToListIndex(listSize: Int): Int {
    if (this < 0) {
        return this + listSize
    } else if (this > listSize - 1) {
        return this - listSize
    } else {
        return this
    }
}