package org.hildan.ipm.helper.galaxy

import java.util.EnumSet

data class ConstantBonuses(
    private val shipsBonus: Bonus,
    private val roomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val managerAssignment: ManagerAssignment = ManagerAssignment(),
    private val market: Market
) {
    private val withoutBeacon = shipsBonus + roomsBonus + managerAssignment.totalBonus
    private val withBeacon = withoutBeacon + beaconBonus

    fun total(beaconActive: Boolean) = if (beaconActive) withBeacon else withoutBeacon
}

data class Galaxy(
    private val constantBonuses: ConstantBonuses,
    private val planets: List<Planet> = PlanetType.values().map { Planet(it) },
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    private val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT)
) {
    private val totalBonus by lazy {
        val constantBonusTotal = constantBonuses.total(Project.BEACON in researchedProjects)
        val projectBonus = researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)
        constantBonusTotal + projectBonus
    }

    private val planetStats = planets.associate { it.type to totalBonus.forPlanet(it.type).applyTo(it.stats) }

    private val planetCosts = planets.associate { it.type to totalBonus.reduceUpgradeCosts(it.upgradeCosts, it.colonyLevel) }

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

    private fun PlanetType.stateReport() = """
        $name:
            Stats:         ${planetStats[this]}
            Upgrade costs: ${planetCosts[this]}
    """.trimIndent()

    override fun toString(): String = PlanetType.values().joinToString("\n") { it.stateReport() }
}
