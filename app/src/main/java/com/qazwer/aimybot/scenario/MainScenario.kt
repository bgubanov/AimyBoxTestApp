package com.qazwer.aimybot.scenario

import com.justai.jaicf.channel.aimybox.aimybox
import com.justai.jaicf.helpers.ssml.break500ms
import com.justai.jaicf.helpers.ssml.breakMs
import com.justai.jaicf.model.scenario.Scenario
import com.qazwer.aimybot.scenario.MainScenarioStates.*

/**
 * @author : bgubanov
 * @since : 22.10.2020
 **/

enum class MainScenarioStates(val statePath: String) {
    LAUNCH("/launch"),
    MAIN("/main"),
    CANCEL_GAME("${MAIN.statePath}/cancel"),
    START_BNC_GAME("${MAIN.statePath}/startBnCGame")
}

object MainScenario : Scenario(
    dependencies = listOf(BullsAndCowsMainScenario)
) {


    init {
        state(LAUNCH.statePath) {
            activators {
                catchAll()
            }
            action {
                reactions.say("Привет, я умею играть в \"Быки и коровы\". Хотите сыграть?")
                reactions.buttons("Да", "Нет")
                reactions.go(MAIN.statePath)
            }
        }

        state(MAIN.statePath) {
            state(CANCEL_GAME.statePath) {
                activators {
                    regex(".*нет.*".toRegex(RegexOption.IGNORE_CASE))
                }

                action {
                    reactions.say("Мне жаль. Но если захотите поиграть, скажите \"Давай поиграем в быки и коровы\"")
                    reactions.aimybox?.endConversation()
                    reactions.goBack()
                }
            }

            state("help") {
                activators {
                    regex(".*помощь.*".toRegex(RegexOption.IGNORE_CASE))
                    regex(".*инфо.*".toRegex(RegexOption.IGNORE_CASE))
                    regex(".*привет.*".toRegex(RegexOption.IGNORE_CASE))
                    regex("/help".toRegex(RegexOption.IGNORE_CASE))
                }

                action {
                    reactions.say("Привет, я умею играть в \"Быки и коровы\". Хотите сыграть?")
                    reactions.buttons("Да", "Нет")
                    reactions.goBack()
                }
            }


            state(START_BNC_GAME.statePath) {
                activators {
                    regex(Regex(".*быки.*", RegexOption.IGNORE_CASE))
                    regex(Regex(".*да.*", RegexOption.IGNORE_CASE))
                }

                action {
                    reactions.say("Это игра в \"Быки и коровы\". Один игрок загадывает число," +
                            " а второй пытается его угадать своими предположениями. Вы готовы начать?"
                    )
                    reactions.buttons("Да", "Нет")
                }

                state("yes") {
                    activators {
                        regex(".*да.*".toRegex(RegexOption.IGNORE_CASE))
                    }

                    action {
                        reactions.go(BullsAndCowsMainScenarioStates.START_GAME_STATE.statePath)
                    }
                }

                state("no") {
                    activators {
                        regex(".*нет.*".toRegex(RegexOption.IGNORE_CASE))
                    }

                    action {
                        reactions.go(CANCEL_GAME.statePath)
                    }
                }

                fallback {
                    reactions.say("Я вас не понимаю. Вы готовы начать?")
                    reactions.buttons("Да", "Нет")
                }
            }

            fallback {
                reactions.say("Мне есть что сказать... но не сейчас")
            }
        }
    }
}