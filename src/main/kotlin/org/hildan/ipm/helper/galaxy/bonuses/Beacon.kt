package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.utils.completedBy

private val allPlanets = Planet.values()

enum class BeaconPlanetRange(val planets: List<Planet>) {
    RANGE_1_4(1, 4),
    RANGE_5_7(5, 7),
    RANGE_8_10(8, 10),
    RANGE_11_13(11, 13),
    RANGE_14_16(14, 16),
    RANGE_17_19(17, 19),
    RANGE_20_22(20, 22),
    RANGE_23_25(23, 25),
    RANGE_26_28(26, 28),
    RANGE_29_31(29, 31);
    // TODO complete ranges when PlanetType is complete

    constructor(from: Int, to: Int): this(allPlanets.slice((from - 1) until to))
}

object Beacon {

    fun bonus(bonuses: Map<BeaconPlanetRange, PlanetBonus>) = Bonus(
        perPlanet = bonuses
            .flatMap { (r, b) -> r.planets.map { it to b } }
            .toMap()
            .completedBy { PlanetBonus.NONE } // TODO remove this default when all planets are covered
    )
}
