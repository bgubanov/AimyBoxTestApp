package com.qazwer.aimybot.scenario

import com.justai.jaicf.model.scenario.Scenario
import com.qazwer.aimybot.bot.BnCGame
import com.qazwer.aimybot.scenario.BnCUserScenarioStates.*
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenario.maxValue
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenario.minValue
import kotlin.random.Random

/**
 * @author : bgubanov
 * @since : 22.10.2020
 **/

enum class BnCUserScenarioStates(val statePath: String) {
    START_GAME_WITH_USER("/bncPlayerStart"),
    GAME_LOOP_STATE("${START_GAME_WITH_USER.statePath}/gameLoop"),
    END_GAME("${GAME_LOOP_STATE.statePath}/endGame"),
}

object BnCUserScenario : Scenario() {

    init {
        state(START_GAME_WITH_USER.statePath) {
            action {
                val game = BnCGame(context)
                game.minValue = minValue
                game.maxValue = maxValue
                game.stepCounter = 0
                reactions.say("Игрок загадывает число от $minValue до $maxValue")
                reactions.buttons("Я загадал")
            }

            state(GAME_LOOP_STATE.statePath) {
                activators {
                    regex(".*загадал.*".toRegex(RegexOption.IGNORE_CASE))
                }

                action {
                    val game = BnCGame(context)

                    val randomNumber = Random.nextInt(game.minValue ?: minValue, (game.maxValue ?: maxValue) + 1)
                    game.conceivedNumber = randomNumber
                    println("conceived: $randomNumber, minValue: $game.")
                    reactions.sayRandom(
                        "Я думаю, ваше число может быть $randomNumber", "Ваше число $randomNumber?",
                        "Может быть, $randomNumber?", "$randomNumber?"
                    )

                    game.incStepCounter()
                    reactions.buttons("Больше", "Меньше", "Ты угадал")
                }

                state("bigger") {
                    activators {
                        regex(".*больше.*".toRegex(RegexOption.IGNORE_CASE))
                    }

                    action {
                        val game = BnCGame(context)
                        val conceivedNumber = game.conceivedNumber ?: minValue
                        when {
                            conceivedNumber >= maxValue -> {
                                reactions.say("Где-то вы меня обманываете. Эх, видимо придётся начать новую игру.")
                                reactions.go(BullsAndCowsMainScenarioStates.RESTART_GAME_STATE.statePath)
                            }
                            conceivedNumber == game.maxValue -> {
                                reactions.say("Могу с уверенностью сказать, что ваше число ${game.maxValue}")
                                game.incStepCounter()
                                reactions.go(END_GAME.statePath)
                            }
                            else -> {
                                game.minValue = conceivedNumber + 1
                                reactions.go(GAME_LOOP_STATE.statePath)
                            }
                        }
                    }
                }

                state("smaller") {
                    activators {
                        regex(".*меньше.*".toRegex(RegexOption.IGNORE_CASE))
                    }

                    action {
                        val game = BnCGame(context)
                        val conceivedNumber = game.conceivedNumber ?: minValue
                        when {
                            conceivedNumber <= minValue -> {
                                reactions.say("Где-то вы меня обманываете. Эх, видимо придётся начать новую игру.")
                                reactions.go(BullsAndCowsMainScenarioStates.RESTART_GAME_STATE.statePath)
                            }
                            conceivedNumber == game.minValue -> {
                                reactions.say("Могу с уверенностью сказать, что ваше число ${game.minValue}")
                                game.incStepCounter()
                                reactions.go(END_GAME.statePath)
                            }
                            else -> {
                                game.maxValue = conceivedNumber - 1
                                reactions.go(GAME_LOOP_STATE.statePath)
                            }
                        }

                    }
                }

                state("endGame") {
                    activators {
                        regex(".*угадал.*".toRegex(RegexOption.IGNORE_CASE))
                    }

                    action {
                        val game = BnCGame(context)
                        val stepCounter = game.stepCounter ?: 0
                        val message = when {
                            stepCounter > 15 -> "Ого, понадобилось целых $stepCounter ${getStepsString(stepCounter)} ходов, чтобы угадать ваше число!"
                            stepCounter in 9..15 -> "Я уже думал, что не угадаю"
                            stepCounter in 5..8 -> "Всего $stepCounter ${getStepsString(stepCounter)}. Но это было непросто."
                            stepCounter in 2..4 -> "Мне просто повезло)"
                            else -> "Как такое возможно? С первой попытки?"
                        }
                        reactions.say(message)
                        reactions.go(BullsAndCowsMainScenarioStates.RESTART_GAME_STATE.statePath)
                    }
                }

                fallback {
                    reactions.say("Я вас не понял. Cкажите больше, меньше или угадал")
                    reactions.buttons("Больше", "Меньше", "Ты угадал")
                }
            }
        }
    }
}