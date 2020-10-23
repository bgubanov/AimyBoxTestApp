package com.justai.jaicf.template

import com.justai.jaicf.context.BotContext

/**
 * @author : bgubanov
 * @since : 21.10.2020
 **/

class BnCGame(botContext: BotContext) {
    var conceivedNumber: Int? by botContext.session
    var maxValue: Int? by botContext.session
    var minValue: Int? by botContext.session
    var stepCounter: Int? by botContext.session

    fun clear() {
        conceivedNumber = null
        maxValue = null
        minValue = null
        stepCounter = null
    }

    fun incStepCounter() {
        stepCounter = (stepCounter?: 0) + 1
    }
}

enum class PlayerType {
    COMPUTER, USER
}