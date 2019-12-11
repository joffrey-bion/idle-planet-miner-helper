package org.hildan.ipm.helper

import java.util.EnumSet

data class Galaxy(
    private val shipsAndRoomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val managerAssignment: ManagerAssignment = ManagerAssignment(),
    private val planets: List<Planet> = PlanetType.values().map { Planet(it) },
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    private val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT)
) {
    private val actualBeaconBonus = if (Project.BEACON in researchedProjects) beaconBonus else Bonus.NONE

    private val projectBonus = researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)

    private val globalBonus = shipsAndRoomsBonus + managerAssignment.totalBonus + actualBeaconBonus + projectBonus

    private val planetStats = planets.associate { it.type to globalBonus.forPlanet(it.type).applyTo(it.stats) }

    private inline fun withChangedPlanet(planet: PlanetType, transform: (Planet) -> Planet) : Galaxy = copy(
        planets = planets.map { if (it.type == planet) transform(it) else it }
    )

    fun withLevels(planet: PlanetType, mine: Int, ships: Int, cargo: Int): Galaxy =
            withChangedPlanet(planet) { it.copy(mineLevel = mine, shipLevel = ships, cargoLevel = cargo) }

    fun withColony(planet: PlanetType, level: Int, bonus: PlanetBonus): Galaxy =
            withChangedPlanet(planet) { it.copy(colonyLevel = level, colonyBonus = bonus) }

    fun withProject(project: Project) : Galaxy = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children
    )

    private val Planet.actualMineRateByOreType: Map<OreType, Double>
        get() {
            val actualStats = planetStats[type] ?: error("Planet $type not found")
            val getRatio = { it: OrePart ->
                if (Project.ORE_TARGETING in researchedProjects && it.oreType == preferredOreType) {
                    it.ratio + 0.15
                } else {
                    it.ratio
                }
            }
            return type.oreDistribution.associate { it.oreType to (actualStats.mineRate * getRatio(it)) }
        }

    override fun toString(): String = """
        Planets:
            ${planets.joinToString("\n            ") { "${it.type.name}: ${it.actualMineRateByOreType}" }}
    """.trimIndent()
}
