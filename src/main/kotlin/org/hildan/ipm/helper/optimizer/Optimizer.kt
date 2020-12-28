package org.hildan.ipm.helper.optimizer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.resources.Resources
import kotlin.time.Duration

class Optimizer(
    initialGalaxy: Galaxy,
    private val searchDepth: Int = 4,
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
                // TODO prioritize expanding states with little timeToRoi1, and don't expand if already exceeding the
                // timeToRoi1 of a fully expanded path
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
    val timeToReach: Duration,
) {
    fun timeToRoi1(initialGalaxy: Galaxy): Duration {
        val incomeRateDiff = galaxy.totalIncomeRate - initialGalaxy.totalIncomeRate
        if (incomeRateDiff == ValueRate.ZERO) {
            return Duration.INFINITE
        }
        val timeToGetMoneyBack = requiredCashSoFar / incomeRateDiff
        return timeToReach + timeToGetMoneyBack
    }

    fun expand(): List<State> = galaxy.possibleActions().map { transition(it) }

    // TODO cache states by set of actions applied since initial state to avoid unnecessary allocations and computations
    // TODO evict cache using the size of the set of actions (if we output 5 actions so far, we can evict cache
    //  elements with 5 or less actions because the exploration of new states will always have more). Maybe use a
    //  2-level map based on size then set?
    private fun transition(action: Action): State {
        val appliedAction = action.performOn(galaxy)
        return State(
            appliedAction.newGalaxy,
            actionsFromStart + appliedAction,
            requiredCashSoFar + appliedAction.requiredCash,
            requiredResourcesSoFar + appliedAction.requiredResources,
            timeToReach + appliedAction.time,
        )
    }

    companion object {

        fun initial(galaxy: Galaxy) = State(
            galaxy = galaxy,
            actionsFromStart = emptyList(),
            requiredCashSoFar = Price.ZERO,
            requiredResourcesSoFar = Resources.NOTHING,
            timeToReach = Duration.ZERO,
        )
    }
}

