package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.utils.completedBy

private val allPlanets = Planet.values()

data class BeaconBonus(
    val from: Int,
    val to: Int,
    val bonus: PlanetBonus
)

val BeaconBonus.planets: List<Planet> get() = allPlanets.slice((from - 1) until to)

object Beacon {

    fun bonus(bonuses: List<BeaconBonus>) = Bonus(
        perPlanet = bonuses
            .flatMap { it.planets.map { p -> p to it.bonus } }
            .toMap()
            .completedBy { PlanetBonus.NONE }
    )
}
