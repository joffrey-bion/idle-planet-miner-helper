package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.utils.asEMap
import java.util.EnumMap

data class Manager(
    val name: String,
    val planetBonus: PlanetBonus,
    val globalBonus: Bonus = Bonus.NONE
) {
    fun toBonus(associatedPlanet: PlanetType): Bonus =
            globalBonus + Bonus(perPlanet = mapOf(associatedPlanet to planetBonus).asEMap { PlanetBonus.NONE })
}

data class ManagerAssignment(
    private val assignedManagers: Map<PlanetType, Manager> = EnumMap(PlanetType::class.java)
) {
    val bonus = assignedManagers
        .map { (p, m) -> m.toBonus(p) }
        .fold(Bonus.NONE) { b1, b2 -> b1 + b2}
}
