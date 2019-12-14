package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.PlanetType
import org.hildan.ipm.helper.galaxy.Price
import java.time.Duration

data class AppliedAction(
    val action: Action,
    val newGalaxy: Galaxy,
    val cost: Price,
    val time: Duration
)

fun Galaxy.possibleActions(): List<AppliedAction> {


    TODO()
}

sealed class Action {

    abstract fun perform(galaxy: Galaxy): Galaxy

    data class UpgradeMine(
        val planet: PlanetType,
        val additionalLevels: Int = 1
    ) : Action() {
        override fun perform(galaxy: Galaxy): Galaxy = galaxy.withChangedPlanet(planet) {
                p -> p.copy(mineLevel = p.mineLevel + additionalLevels)
        }
    }
}
