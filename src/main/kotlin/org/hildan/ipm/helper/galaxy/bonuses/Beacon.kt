package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.utils.completedBy

data class BeaconRangeBonus(
    val from: Int,
    val to: Int,
    val bonus: PlanetBonus
)

private val BeaconRangeBonus.planets: List<Planet> get() = Planet.values().slice((from - 1) until to)

object Beacon {

    fun bonus(bonuses: List<BeaconRangeBonus>) = Bonus(
        perPlanet = bonuses
            .flatMap { it.planets.map { p -> p to it.bonus } }
            .toMap()
            .completedBy { PlanetBonus.NONE }
    )
}
