package com.qazwer.aimybot.scenario

/**
 * @author : bgubanov
 * @since : 22.10.2020
 **/

fun getStepsString(stepCounter: Int): String {
    return when {
        stepCounter % 100 in 11..14 -> "ходов"
        stepCounter % 10 == 1 -> "ход"
        stepCounter % 10 in 2..4 -> "хода"
        else -> "ходов"
    }
}