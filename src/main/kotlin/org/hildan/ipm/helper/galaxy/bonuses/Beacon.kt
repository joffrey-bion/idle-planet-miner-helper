package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.Planet

data class BeaconRangeBonus(
    val from: Int,
    val to: Int,
    val bonus: PlanetBonus,
)

private val BeaconRangeBonus.planets: List<Planet> get() = Planet.values().slice((from - 1) until to)

fun List<BeaconRangeBonus>.asSingleBonus() = Bonus.perPlanet(flatMap { it.planets.map { p -> p to it.bonus } }.toMap())
