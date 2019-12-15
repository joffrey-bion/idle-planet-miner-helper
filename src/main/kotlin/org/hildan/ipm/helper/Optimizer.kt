package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Price
import java.time.Duration
import kotlin.math.roundToInt

class Optimizer(
    initialGalaxy: Galaxy,
    private val searchDepth: Int = 3
) {
    private var currentState = State(
        galaxy = initialGalaxy,
        actionsFromStart = emptyList(),
        costToReach = Price.ZERO,
        timeToReach = Duration.ZERO
    )

    fun generateActions(): Sequence<Action> = sequence {
        val nextBestState = computeNextBestState()
        yield(nextBestState.actionsFromStart.first())
        currentState = nextBestState
    }

    private fun computeNextBestState(): State {
        var states = listOf(currentState)
        repeat(searchDepth) {
            states = states.flatMap { it.expand() }
        }
        return states.minBy { it.timeToRoi1(currentState.galaxy) }!!
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
