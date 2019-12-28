package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.utils.completedBy
import java.util.EnumMap

data class Manager(
    val name: String,
    val planetBonus: PlanetBonus,
    val globalBonus: Bonus = Bonus.NONE
) {
    fun toBonus(associatedPlanet: Planet): Bonus =
            globalBonus + Bonus(perPlanet = mapOf(associatedPlanet to planetBonus).completedBy { PlanetBonus.NONE })
}

data class ManagerAssignment(
    private val assignedManagers: Map<Planet, Manager> = EnumMap(Planet::class.java)
) {
    val bonus = assignedManagers
        .map { (p, m) -> m.toBonus(p) }
        .fold(Bonus.NONE) { b1, b2 -> b1 + b2}
}
