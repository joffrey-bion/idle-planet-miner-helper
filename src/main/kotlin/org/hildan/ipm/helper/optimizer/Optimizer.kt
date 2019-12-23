package org.hildan.ipm.helper.optimizer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.INFINITE_TIME
import java.time.Duration

class Optimizer(
    initialGalaxy: Galaxy,
    private val searchDepth: Int = 4
) {
    private var currentGalaxy = initialGalaxy

    fun generateActions(): Sequence<AppliedAction> = sequence {
        while (true) {
            val appliedAction = runBlocking { computeNextBestAction() }
            val newGalaxy = appliedAction.newGalaxy
            yield(appliedAction)

            // these could be included in the search by providing actual income rate changes (make them "real" actions)
            if (currentGalaxy.maxIncomeSmeltRecipe != newGalaxy.maxIncomeSmeltRecipe) {
                yield(Action.SwitchSmeltRecipe(newGalaxy.maxIncomeSmeltRecipe!!).performOn(newGalaxy))
            }
            if (currentGalaxy.maxIncomeCraftRecipe != newGalaxy.maxIncomeCraftRecipe) {
                yield(Action.SwitchCraftRecipe(newGalaxy.maxIncomeCraftRecipe!!).performOn(newGalaxy))
            }

            currentGalaxy = newGalaxy
        }
    }

    private suspend fun computeNextBestAction(): AppliedAction {
        var states = listOf(State.initial(currentGalaxy))
        withContext(Dispatchers.Default) {
            repeat(searchDepth) {
                states = states.map { async { it.expand() } }.flatMap { it.await() }
            }
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
        if (incomeRateDiff == ValueRate.ZERO) {
            return INFINITE_TIME
        }
        val timeToGetMoneyBack = requiredCashSoFar / incomeRateDiff
        return timeToReach + timeToGetMoneyBack
    }

    fun expand(): List<State> = galaxy.possibleActions().map { transition(it) }

    private fun transition(action: AppliedAction): State =
            State(
                action.newGalaxy,
                actionsFromStart + action,
                requiredCashSoFar + action.requiredCash,
                requiredResourcesSoFar + action.requiredResources,
                timeToReach + action.time
            )

    companion object {

        fun initial(galaxy: Galaxy) = State(
            galaxy = galaxy,
            actionsFromStart = emptyList(),
            requiredCashSoFar = Price.ZERO,
            requiredResourcesSoFar = Resources.NOTHING,
            timeToReach = Duration.ZERO
        )
    }
}

