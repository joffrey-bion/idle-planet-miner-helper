package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.PlanetType
import org.hildan.ipm.helper.galaxy.Price
import org.hildan.ipm.helper.galaxy.Project
import java.time.Duration

data class AppliedAction(
    val action: Action,
    val newGalaxy: Galaxy,
    val cost: Price,
    val time: Duration
)

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
        .filter { areAccessible(it.requiredResources) }
        .map { Action.Research(it).performOn(this) }
    return buyPlanetActions + upgradeActions + researchActions
}

sealed class Action {

    abstract fun performOn(galaxy: Galaxy): AppliedAction

    data class BuyPlanet(val planet: PlanetType) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = AppliedAction(
            action = this,
            newGalaxy = galaxy.withBoughtPlanet(planet),
            cost = planet.unlockPrice,
            time = Duration.ZERO
        )
    }

    data class UpgradeMine(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = AppliedAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(mineLevel = p.mineLevel + additionalLevels)
            },
            cost = galaxy.planetCosts[planet]!!.mineUpgrade,
            time = Duration.ZERO
        )
    }

    data class UpgradeShip(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = AppliedAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(shipLevel = p.shipLevel + additionalLevels)
            },
            cost = galaxy.planetCosts[planet]!!.shipUpgrade,
            time = Duration.ZERO
        )
    }

    data class UpgradeCargo(val planet: PlanetType, val additionalLevels: Int = 1) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = AppliedAction(
            action = this,
            newGalaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(cargoLevel = p.cargoLevel + additionalLevels)
            },
            cost = galaxy.planetCosts[planet]!!.cargoUpgrade,
            time = Duration.ZERO
        )
    }

    data class Research(val project: Project): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = AppliedAction(
            action = this,
            newGalaxy = galaxy.withProject(project),
            cost = project.requiredResources.getTotalCost(galaxy.constantBonuses.market),
            time = project.requiredResources.getApproximateTime(galaxy.nbSmelters, galaxy.nbCrafters)
        )
    }
}
