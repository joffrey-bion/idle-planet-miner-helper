package org.hildan.ipm.helper

import java.util.EnumMap
import java.util.EnumSet

data class Galaxy(
    private val shipsAndRoomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val planets: List<Planet> = PlanetType.values().map { Planet(it) },
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    private val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT),
    private val assignedManagers: Map<PlanetType, Manager> = EnumMap(PlanetType::class.java)
) {
    private val managerBonus = assignedManagers
        .map { (p, m) -> m.toBonus(p) }
        .map { it.scale(shipsAndRoomsBonus.managersMultiplier) }
        .fold(Bonus.NONE) { b1, b2 -> b1 + b2}

    private val actualBeaconBonus = if (Project.BEACON in researchedProjects) beaconBonus else Bonus.NONE

    private val totalBonus = shipsAndRoomsBonus + managerBonus + actualBeaconBonus

    private val planetBonuses = PlanetType.values().associate { it to totalBonus.forPlanet(it) }

    private inline fun withChangedPlanet(planet: PlanetType, transform: (Planet) -> Planet) : Galaxy = copy(
        planets = planets.map { if (it.type == planet) transform(it) else it }
    )

    fun withLevels(planet: PlanetType, mine: Int, ships: Int, cargo: Int): Galaxy =
            withChangedPlanet(planet) { it.copy(mineLevel = mine, shipLevel = ships, cargoLevel = cargo) }

    fun withManagerAssignedTo(manager: Manager, planet: PlanetType) : Galaxy {
        val otherManagers = findAssignedPlanet(manager)?.let { assignedManagers - it } ?: assignedManagers
        return copy(assignedManagers = otherManagers + mapOf(planet to manager))
    }

    fun withManagerUnassigned(manager: Manager): Galaxy {
        val formerPlanet = findAssignedPlanet(manager) ?:error("Manager $manager was not assigned")
        return copy(assignedManagers = assignedManagers - formerPlanet)
    }

    private fun findAssignedPlanet(manager: Manager): PlanetType? =
            assignedManagers.filterValues { mgr -> mgr == manager }.map { it.key }.firstOrNull()

    fun withProject(project: Project) : Galaxy = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children
    )

    private val Planet.totalBonus: PlanetBonus
        get() = planetBonuses[type] ?: error("Missing planet $this!")

    // TODO account for colony bonus
    private val Planet.actualMineRate: Double
        get() = ownMineRate * totalBonus.mineRate

    private val Planet.actualMineRateByOreType: Map<OreType, Double>
        get() {
            val getRatio = { it: OrePart ->
                if (Project.ORE_TARGETING in researchedProjects && it.oreType == preferredOreType) {
                    it.ratio + 0.15
                } else {
                    it.ratio
                }
            }
            return type.oreDistribution.associate { it.oreType to (actualMineRate * getRatio(it)) }
        }

    override fun toString(): String = """
        Planets:
            ${planets.joinToString("\n            ") { "${it.type.name}: ${it.actualMineRateByOreType}" }}
    """.trimIndent()
}
