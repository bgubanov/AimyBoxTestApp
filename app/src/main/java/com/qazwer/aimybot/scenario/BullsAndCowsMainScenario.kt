package com.qazwer.aimybot.scenario

import com.justai.jaicf.channel.aimybox.aimybox
import com.justai.jaicf.helpers.ssml.break300ms
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.template.BnCGame
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenarioStates.RESTART_GAME_STATE
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenarioStates.START_GAME_STATE

/**
 * @author : bgubanov
 * @since : 22.10.2020
 **/

enum class BullsAndCowsMainScenarioStates(val statePath: String) {
    START_GAME_STATE("/bncGame"),
    RESTART_GAME_STATE("/restart"),
}

object BullsAndCowsMainScenario : Scenario(
    dependencies = listOf(BnCUserScenario, BnCComputerScenario)
) {

    const val minValue = 1
    const val maxValue = 1000

    init {
        state(START_GAME_STATE.statePath) {
            action {
                reactions.say("Хорошо, давайте начнём. Кто будет загадывать число: я или вы?")
                reactions.buttons("Я", "Ты")
            }

            state("me") {
                activators { regex("(я)".toRegex(RegexOption.IGNORE_CASE)) }
                action { reactions.go(BnCUserScenarioStates.START_GAME_WITH_USER.statePath) }
            }

            state("you") {
                activators { regex(".*(ты|вы).*".toRegex(RegexOption.IGNORE_CASE)) }
                action { reactions.go(BnCComputerScenarioStates.START_GAME_WITH_COMPUTER.statePath) }
            }

            fallback {
                reactions.say("Я вас не понимаю, скажите: я или ты")
                reactions.buttons("Я", "Ты")
            }
        }

        state(RESTART_GAME_STATE.statePath) {
            action {
                BnCGame(context).clear()
                reactions.say("Хотите сыграть ещё раз?")
                reactions.buttons("Да", "Нет")
            }

            state("yes") {
                activators {
                    regex(".*да.*".toRegex(RegexOption.IGNORE_CASE))
                }
                action {
                    reactions.go(START_GAME_STATE.statePath)
                }
            }
            state("no") {
                activators {
                    regex(".*нет.*".toRegex(RegexOption.IGNORE_CASE))
                }

                action {
                    reactions.say("Ну что ж. Был рад поиграть, если захотите сыграть ещё раз, " +
                            "скажите, что хотите сыграть в быки и коровы")
                    reactions.aimybox?.endConversation()
                    reactions.go(MainScenarioStates.MAIN.statePath)
                }
            }

            fallback {
                reactions.say("Я вас не понял. Хотите сыграть ещё раз?")
                reactions.buttons("Да", "Нет")
            }
        }
    }
}