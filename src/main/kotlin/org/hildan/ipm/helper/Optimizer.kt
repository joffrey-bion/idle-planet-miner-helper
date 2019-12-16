package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Price
import org.hildan.ipm.helper.galaxy.resources.Resources
import java.time.Duration

class Optimizer(
    initialGalaxy: Galaxy,
    private val searchDepth: Int = 4
) {
    private var currentGalaxy = initialGalaxy

    fun generateActions(): Sequence<AppliedAction> = generateSequence {
        val appliedAction = computeNextBestAction()
        currentGalaxy = appliedAction.newGalaxy
        appliedAction
    }

    private fun computeNextBestAction(): AppliedAction {
        var states = listOf(State(
            galaxy = currentGalaxy,
            actionsFromStart = emptyList(),
            requiredCashSoFar = Price.ZERO,
            requiredResourcesSoFar = Resources.NOTHING,
            timeToReach = Duration.ZERO
        ))
        repeat(searchDepth) {
            states = states.flatMap { it.expand() }
        }
        val bestEndState = states.minBy { it.timeToRoi1(currentGalaxy) }!!
        return bestEndState.actionsFromStart.first()
    }
}

data class State(
    val galaxy: Galaxy,
    val actionsFromStart: List<AppliedAction>,
    val requiredCashSoFar: Price,
    val requiredResourcesSoFar: Resources,
    val timeToReach: Duration
) {
    fun timeToRoi1(initialGalaxy: Galaxy): Duration {
        val incomeRateDiff = galaxy.totalIncomeRate - initialGalaxy.totalIncomeRate
        val timeToGetMoneyBack = requiredCashSoFar / incomeRateDiff
        return timeToReach + timeToGetMoneyBack
    }
}

fun State.expand(): List<State> = galaxy.possibleActions().map {
    State(
        it.newGalaxy,
        actionsFromStart + it,
        requiredCashSoFar + it.requiredCash,
        requiredResourcesSoFar + it.requiredResources,
        timeToReach + it.time
    )
}
