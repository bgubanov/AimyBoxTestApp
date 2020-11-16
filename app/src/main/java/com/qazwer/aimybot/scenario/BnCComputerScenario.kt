package com.qazwer.aimybot.scenario

import com.justai.jaicf.model.scenario.Scenario
import com.qazwer.aimybot.bot.BnCGame
import com.qazwer.aimybot.scenario.BnCComputerScenarioStates.GAME_LOOP_STATE
import com.qazwer.aimybot.scenario.BnCComputerScenarioStates.START_GAME_WITH_COMPUTER
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenario.maxValue
import com.qazwer.aimybot.scenario.BullsAndCowsMainScenario.minValue
import kotlin.random.Random

/**
 * @author : bgubanov
 * @since : 22.10.2020
 **/

enum class BnCComputerScenarioStates(val statePath: String) {
    START_GAME_WITH_COMPUTER("/bncComputerStart"),
    GAME_LOOP_STATE("${START_GAME_WITH_COMPUTER.statePath}/gameLoop"),
}

object BnCComputerScenario : Scenario() {

    init {
        state(START_GAME_WITH_COMPUTER.statePath) {
            action {
                reactions.say("Тогда я загадываю число.")
                reactions.say("Я загадал число от $minValue до $maxValue. Попробуйте угадать его")
                val game = BnCGame(context)
                game.stepCounter = 0
                game.conceivedNumber = Random.nextInt(minValue, maxValue)
                game.maxValue = maxValue
                game.minValue = minValue
                println("conceivedNumber: ${game.conceivedNumber}")
            }

            state(GAME_LOOP_STATE.statePath) {
                activators {
                    regex(".*(\\d+).*".toRegex(RegexOption.IGNORE_CASE))
                }

                action {
                    val numberString = request.input.replace(Regex("\\D"), "")
                    val number = numberString.toIntOrNull()
                    val game = BnCGame(context)
                    val conceivedNumber = game.conceivedNumber ?: 0
                    val minValue = game.minValue ?: minValue
                    val maxValue = game.maxValue ?: maxValue
                    println("number: $number conceived: ${game.conceivedNumber}")
                    when {
                        number == null -> reactions.say("Я вас не понял, повторите, пожалуйста.")
                        number !in minValue..maxValue -> reactions.say("Не забывайте, я загадал число от $minValue до $maxValue")
                        conceivedNumber < number -> {
                            reactions.sayRandom(
                                "Моё число меньше",
                                "Меньше",
                                "Бери ниже",
                                "Поменьше"
                            )
                            game.incStepCounter()
                        }
                        conceivedNumber > number -> {
                            reactions.sayRandom(
                                "Моё число больше",
                                "Больше",
                                "Бери выше",
                                "Побольше"
                            )
                            game.incStepCounter()
                        }
                        else -> {
                            reactions.say("Вы угадали!")
                            val stepCounter = game.stepCounter ?: 0
                            val message = when {
                                stepCounter > 15 -> "Вам понадобилось $stepCounter ${getStepsString(stepCounter)} ходов," +
                                        " чтобы угадать моё число. Я думаю, вы можете справиться быстрее."
                                stepCounter in 9..15 -> "Неплохой результат." +
                                        " Всего $stepCounter ${getStepsString(stepCounter)}"
                                stepCounter in 5..8 -> "Всего $stepCounter ${getStepsString(stepCounter)}. Вы просто гений!"
                                stepCounter in 2..4 -> "Это просто невозможный результат! " +
                                        "$stepCounter ${getStepsString(stepCounter)}, Невероятно!"
                                else -> "Вы где-то подсмотрели ответ, верно?"
                            }
                            reactions.say(message)
                            reactions.go(BullsAndCowsMainScenarioStates.RESTART_GAME_STATE.statePath)
                        }
                    }
                }

                fallback {
                    reactions.say("Я вас не понял, повторите своё число, пожалуйста.")
                }
            }
        }
    }
}
