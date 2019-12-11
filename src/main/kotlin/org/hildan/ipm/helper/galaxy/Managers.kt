package org.hildan.ipm.helper.galaxy

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
    val totalBonus = assignedManagers
        .map { (p, m) -> m.toBonus(p) }
        .fold(Bonus.NONE) { b1, b2 -> b1 + b2}

    fun withManagerAssignedTo(manager: Manager, planet: PlanetType) : ManagerAssignment {
        val otherManagers = findAssignedPlanet(manager)?.let { assignedManagers - it } ?: assignedManagers
        return copy(assignedManagers = otherManagers + mapOf(planet to manager))
    }

    fun withManagerUnassigned(manager: Manager): ManagerAssignment {
        val formerPlanet = findAssignedPlanet(manager) ?: error("Manager $manager was not assigned")
        return copy(assignedManagers = assignedManagers - formerPlanet)
    }

    private fun findAssignedPlanet(manager: Manager): PlanetType? =
            assignedManagers.filterValues { mgr -> mgr == manager }.map { it.key }.firstOrNull()
}
