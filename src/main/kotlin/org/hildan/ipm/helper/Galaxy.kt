package org.hildan.ipm.helper

import java.util.EnumSet

data class Galaxy(
    private val longTermBonus: Bonus,
    private val beaconBonus: BeaconBonus,
    private val planets: List<Planet> = PlanetType.values().map { Planet(it) },
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    private val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT)
) {
    private inline fun withChangedPlanet(planet: PlanetType, transform: (Planet) -> Planet) : Galaxy = copy(
        planets = this.planets.map { if (it.type == planet) transform(it) else it }
    )

    fun withLevels(planet: PlanetType, mine: Int, ships: Int, cargo: Int): Galaxy =
            withChangedPlanet(planet) { it.copy(mineLevel = mine, shipLevel = ships, cargoLevel = cargo) }

    fun withManager(manager: Manager, planet: PlanetType) : Galaxy = withChangedPlanet(planet) {
        it.copy(managerBonus = manager.planetBonus)
    }

    fun withProject(project: Project) : Galaxy = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children
    )

    private val Planet.localBeaconBonus: PlanetBonus
        get() = if (Project.BEACON in researchedProjects) beaconBonus[this.type] else PlanetBonus.NONE

    private val Planet.beaconMiningBonus: Double
        get() = localBeaconBonus.mineRate

    // TODO account for colony bonus
    private val Planet.actualMineRate: Double
        get() = ownMineRate * longTermBonus.globalPlanetBonus.mineRate * beaconMiningBonus

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
            ${planets.map { "${it.type.name}: ${it.actualMineRateByOreType}" }.joinToString("\n            ")}
    """.trimIndent()
}
