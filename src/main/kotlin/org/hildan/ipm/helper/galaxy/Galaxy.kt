package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.div
import java.time.Duration
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

    companion object {
        val NONE = ConstantBonuses(
            shipsBonus = Bonus.NONE,
            roomsBonus = Bonus.NONE,
            beaconBonus = Bonus.NONE,
            managerAssignment = ManagerAssignment(),
            market = Market()
        )
    }
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
    private val highestAccessibleOreType: OreType? = planets.map { it.preferredOreType }.max()

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
            val oreTargeting = Project.ORE_TARGETING in researchedProjects
            val rates =  planets.flatMap {
                planetStats[it.type]!!.deliveryRateByOreType(it, oreTargeting)
            }
            return rates.map { constantBonuses.market.getSellPrice(it.oreType) * it.rate }.sum()
        }

    fun withBoughtPlanet(planet: PlanetType) : Galaxy = copy(
        planets = planets + Planet(planet),
        unlockedPlanets = unlockedPlanets - planet
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

    fun withProjects(projects: List<Project>) : Galaxy {
        var nextGalaxy = this
        projects.forEach { nextGalaxy = nextGalaxy.withProject(it) }
        return nextGalaxy
    }

    fun Resources.areAccessible(): Boolean {
        if (highestAlloy != null && nbSmelters == 0) return false
        if (highestItem != null && nbCrafters == 0) return false
        val oresAccessible = highestOre?.let { o -> highestAccessibleOreType?.let { o <= it } } ?: true
        val alloysAccessible = highestAlloy?.let { it <= highestUnlockedAlloyRecipe } ?: true
        val itemsAccessible = highestItem?.let { it <= highestUnlockedItemRecipe } ?: true
        return oresAccessible && alloysAccessible && itemsAccessible
    }

    fun getTotalCost(resources: Resources): Price =
            resources.resources.map { constantBonuses.market.getSellPrice(it.resourceType) * it.quantity }.sum()

    fun getApproximateTime(resources: Resources): Duration {
        val smeltTime = resources.totalSmeltTimeFromOre / nbSmelters
        val craftTime = resources.totalCraftTimeFromOresAndAlloys/ nbCrafters
        val reducedSmeltTime = totalBonus.production.smeltSpeed.applyAsSpeed(smeltTime)
        val reducedCraftTime = totalBonus.production.craftSpeed.applyAsSpeed(craftTime)
        return if (reducedSmeltTime > reducedCraftTime) reducedSmeltTime else reducedCraftTime
    }

    private fun PlanetType.stateReport() = """
        $name:
            Stats:         ${planetStats[this]}
            Upgrade costs: ${planetCosts[this]}
    """.trimIndent()

    override fun toString(): String = PlanetType.values().joinToString("\n") { it.stateReport() }
}
