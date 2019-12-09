package org.hildan.ipm.helper

import java.util.EnumMap

private val planets = PlanetType.values()

enum class BeaconPlanetRange(val planets: List<PlanetType>) {
    RANGE_1_4(planets.slice(0..3)),
    //    RANGE_5_7(planets.slice(4..6)),
    //    RANGE_8_10(planets.slice(7..9)),
    //    RANGE_11_13(planets.slice(10..13))
    // TODO complete ranges when PlanetType is complete
}

class BeaconBonus(
    vararg bonuses: Pair<BeaconPlanetRange, PlanetBonus>
) {
    private val bonusByPlanet: Map<PlanetType, PlanetBonus> = bonuses
        .flatMap { (r, b) -> r.planets.map { it to b } }
        .toMap(EnumMap<PlanetType, PlanetBonus>(PlanetType::class.java))

    operator fun get(planet: PlanetType): PlanetBonus = bonusByPlanet[planet] ?: error("Missing planet $planet!")
}
