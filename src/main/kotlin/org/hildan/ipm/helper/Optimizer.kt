package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Price
import java.time.Duration
import kotlin.math.roundToInt

class Optimizer(
    initialGalaxy: Galaxy,
    private val searchDepth: Int = 4
) {
    private var currentGalaxy = initialGalaxy

    fun generateActions(): Sequence<Action> = generateSequence {
        val appliedAction = computeNextBestAction()
        currentGalaxy = appliedAction.newGalaxy
        appliedAction.action
    }

    private fun computeNextBestAction(): AppliedAction {
        var states = listOf(State(
            galaxy = currentGalaxy,
            actionsFromStart = emptyList(),
            costToReach = Price.ZERO,
            timeToReach = Duration.ZERO
        ))
        repeat(searchDepth) {
            states = states.flatMap { it.expand() }
        }
        val bestEndState = states.minBy { it.timeToRoi1(currentGalaxy) }!!
        return bestEndState.actionsFromStart.first().performOn(currentGalaxy)
    }
}

data class State(
    val galaxy: Galaxy,
    val actionsFromStart: List<Action>,
    val costToReach: Price,
    val timeToReach: Duration
) {
    fun timeToRoi1(initialGalaxy: Galaxy): Duration {
        val incomeDiffPerSecond = galaxy.totalIncomePerSecond - initialGalaxy.totalIncomePerSecond
        val timeToGetMoneyBack = Duration.ofSeconds((costToReach / incomeDiffPerSecond).roundToInt().toLong())!!
        return timeToReach + timeToGetMoneyBack
    }
}

fun State.expand(): List<State> = galaxy.possibleActions().map {
    State(
        it.newGalaxy,
        actionsFromStart + it.action,
        costToReach + it.cost,
        timeToReach + it.time
    )
}
