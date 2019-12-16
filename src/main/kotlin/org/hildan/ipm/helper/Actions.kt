package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.PlanetType
import org.hildan.ipm.helper.galaxy.Price
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.ValueRate
import org.hildan.ipm.helper.galaxy.resources.Resources
import java.time.Duration

data class AppliedAction(
    val action: Action,
    val newGalaxy: Galaxy,
    val requiredCash: Price,
    val requiredResources: Resources,
    val time: Duration,
    val incomeRateGain: ValueRate
) {
    override fun toString(): String {
        return "$action - Cost: $requiredCash and $requiredResources"
    }
}

fun Galaxy.possibleActions(): List<AppliedAction> {
    val buyPlanetActions = unlockedPlanets.map { Action.BuyPlanet(it).performOn(this) }
    val upgradeActions = planets.flatMap {
        listOf(
            Action.UpgradeMine(it.type, 1).performOn(this),
            Action.UpgradeShip(it.type, 1).performOn(this),
            Action.UpgradeCargo(it.type, 1).performOn(this)
        )
    }
    val researchActions = unlockedProjects
        .filter { it.requiredResources.areAccessible() }
        .map { Action.Research(it).performOn(this) }
    return buyPlanetActions + upgradeActions + researchActions
}

private fun Galaxy.createAction(
    action: Action,
    newGalaxy: Galaxy,
    requiredCash: Price = Price.ZERO,
    requiredResources: Resources = Resources.NOTHING
): AppliedAction {
    val timeWaitingForCash = requiredCash / totalIncomeRate
    val timeWaitingForResources = getApproximateTime(requiredResources)
    return AppliedAction(
        action = action,
        newGalaxy = newGalaxy,
        requiredCash = requiredCash,
        requiredResources = requiredResources,
        time = max(timeWaitingForCash, timeWaitingForResources),
        incomeRateGain = newGalaxy.totalIncomeRate - totalIncomeRate
    )
}

private fun max(d1: Duration, d2: Duration) = if (d1 < d2) d2 else d1

sealed class Action {

    abstract fun performOn(galaxy: Galaxy): AppliedAction

    data class BuyPlanet(val planet: PlanetType) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withBoughtPlanet(planet),
            requiredCash = planet.unlockPrice
        )
    }

    data class UpgradeMine(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(mineLevel = p.mineLevel + additionalLevels)
            },
            requiredCash = galaxy.planetCosts[planet]!!.mineUpgrade
        )
    }

    data class UpgradeShip(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(shipLevel = p.shipLevel + additionalLevels)
            },
            requiredCash = galaxy.planetCosts[planet]!!.shipUpgrade
        )
    }

    data class UpgradeCargo(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(cargoLevel = p.cargoLevel + additionalLevels)
            },
            requiredCash = galaxy.planetCosts[planet]!!.cargoUpgrade
        )
    }

    data class Research(val project: Project): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withProject(project),
            requiredResources = project.requiredResources
        )
    }
}
