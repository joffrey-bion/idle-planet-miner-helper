package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.Resources
import java.util.EnumSet

data class ConstantBonuses(
    private val shipsBonus: Bonus,
    private val roomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val managerAssignment: ManagerAssignment = ManagerAssignment(),
    val market: Market
) {
    private val withoutBeacon = shipsBonus + roomsBonus + managerAssignment.totalBonus
    private val withBeacon = withoutBeacon + beaconBonus

    fun total(beaconActive: Boolean) = if (beaconActive) withBeacon else withoutBeacon
}

data class Galaxy(
    val constantBonuses: ConstantBonuses,
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT),
    private val highestUnlockedAlloyRecipe: AlloyType = AlloyType.BRONZE,
    private val highestUnlockedItemRecipe: ItemType = ItemType.COPPER_WIRE,
    val planets: List<Planet> = emptyList(),
    val unlockedPlanets: Set<PlanetType> = TelescopeLevel(0).unlockedPlanets,
    val nbSmelters: Int = 0,
    val nbCrafters: Int = 0
) {
    private val highestAccessibleOreType: OreType = planets.map { it.preferredOreType }.max()!!

    private val totalBonus by lazy {
        val constantBonusTotal = constantBonuses.total(Project.BEACON in researchedProjects)
        val projectBonus = researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)
        constantBonusTotal + projectBonus
    }

    val planetStats = planets.associate { it.type to totalBonus.forPlanet(it.type).applyTo(it.stats) }

    val planetCosts = planets.associate { it.type to totalBonus.reduceUpgradeCosts(it.upgradeCosts, it.colonyLevel) }

    // TODO create class for income rate
    val totalIncomePerSecond: Price
        get() {
            TODO()
        }

    fun withBoughtPlanet(planet: PlanetType) : Galaxy = copy(
        planets = planets + Planet(planet)
    )

    inline fun withChangedPlanet(planet: PlanetType, transform: (Planet) -> Planet) : Galaxy = copy(
        planets = planets.map { if (it.type == planet) transform(it) else it }
    )

    fun withLevels(planet: PlanetType, mine: Int, ships: Int, cargo: Int): Galaxy =
            withChangedPlanet(planet) { it.copy(mineLevel = mine, shipLevel = ships, cargoLevel = cargo) }

    fun withColony(planet: PlanetType, level: Int, bonus: PlanetBonus): Galaxy =
            withChangedPlanet(planet) { it.copy(colonyLevel = level, colonyBonus = bonus) }

    fun withProject(project: Project) : Galaxy {
        val newPlanets = if (project.telescopeLevel != null) {
            unlockedPlanets + project.telescopeLevel.unlockedPlanets
        } else {
            unlockedPlanets
        }
        val newNbSmelters = if (project == Project.SMELTER) 1 else nbSmelters
        val newNbCrafters = if (project == Project.CRAFTER) 1 else nbCrafters
        return copy(
            unlockedPlanets = newPlanets,
            researchedProjects = researchedProjects + project,
            unlockedProjects = unlockedProjects + project.children - project,
            nbSmelters = newNbSmelters,
            nbCrafters = newNbCrafters
        )
    }

    fun areAccessible(resources: Resources): Boolean {
        val oresAccessible = resources.highestOre?.let { it <= highestAccessibleOreType } ?: true
        val alloysAccessible = resources.highestAlloy?.let { it <= highestUnlockedAlloyRecipe } ?: true
        val itemsAccessible = resources.highestItem?.let { it <= highestUnlockedItemRecipe } ?: true
        return oresAccessible && alloysAccessible && itemsAccessible
    }

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
