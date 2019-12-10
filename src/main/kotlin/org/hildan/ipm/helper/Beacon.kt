package org.hildan.ipm.helper

private val planets = PlanetType.values()

enum class BeaconPlanetRange(val planets: List<PlanetType>) {
    RANGE_1_4(planets.slice(0..3)),
    //    RANGE_5_7(planets.slice(4..6)),
    //    RANGE_8_10(planets.slice(7..9)),
    //    RANGE_11_13(planets.slice(10..13))
    // TODO complete ranges when PlanetType is complete
}

object Beacon {

    fun bonus(vararg bonuses: Pair<BeaconPlanetRange, PlanetBonus>) = Bonus(
        perPlanet = bonuses
            .flatMap { (r, b) -> r.planets.map { it to b } }
            .toMap()
            .asEMap { PlanetBonus.NONE } // TODO remove this default when all planets are covered
    )
}
