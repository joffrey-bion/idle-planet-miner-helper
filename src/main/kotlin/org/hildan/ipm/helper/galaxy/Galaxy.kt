package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.perSecond
import org.hildan.ipm.helper.galaxy.money.sum
import org.hildan.ipm.helper.galaxy.money.sumRates
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.div
import java.time.Duration
import java.util.EnumSet

data class Galaxy(
    val constantBonuses: ConstantBonuses,
    val currentCash: Price = Price(180),
    private val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT),
    val planets: List<Planet> = emptyList(),
    val unlockedPlanets: Set<PlanetType> = TelescopeLevel(0).unlockedPlanets,
    val nbSmelters: Int = 0,
    val nbCrafters: Int = 0,
    val highestUnlockedAlloyRecipe: AlloyType = AlloyType.COPPER_BAR,
    val highestUnlockedItemRecipe: ItemType = ItemType.COPPER_WIRE
) {
    private val totalBonus by lazy {
        val constantBonusTotal = constantBonuses.total(Project.BEACON in researchedProjects)
        val projectBonus = researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)
        constantBonusTotal + projectBonus
    }

    val planetStats = planets.associate { it.type to totalBonus.forPlanet(it.type).applyTo(it.stats) }

    val planetCosts = planets.associate { it.type to totalBonus.reduceUpgradeCosts(it.upgradeCosts, it.colonyLevel) }

    private val accessibleOreTypes: Set<OreType> = planets
        .flatMap { it.type.oreDistribution }
        .map { it.oreType }
        .toSet()

    private val accessibleAlloyTypes: Set<AlloyType> = if (nbSmelters == 0) {
        emptySet()
    } else {
        AlloyType.values().filter { it <= highestUnlockedAlloyRecipe }.toSet()
    }

    private val accessibleItemTypes: Set<ItemType> = if (nbCrafters == 0) {
        emptySet()
    } else {
        ItemType.values().filter { it <= highestUnlockedItemRecipe }.toSet()
    }

    private val maxIncomeSmeltRecipe: AlloyType? = accessibleAlloyTypes.maxBy { getSmeltingIncome(it) }

    private val maxIncomeCraftRecipe: ItemType? = accessibleItemTypes.maxBy { getCraftingIncome(it) }

    val totalIncomeRate: ValueRate
        get() {
            val oreTargeting = Project.ORE_TARGETING in researchedProjects
            val rates =  planets.flatMap { planetStats[it.type]!!.deliveryRateByOreType(it, oreTargeting) }
            val oreIncome = rates.map { it.oreType.currentValue * it.rate.perSecond() }.sumRates()

            val smeltIncome = maxIncomeSmeltRecipe?.let { getSmeltingIncome(it) } ?: ValueRate.ZERO
            val craftIncome = maxIncomeCraftRecipe?.let { getCraftingIncome(it) } ?: ValueRate.ZERO
            return oreIncome + smeltIncome + craftIncome
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

    fun withMineLevel(planet: PlanetType, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(mineLevel = level) }

    fun withShipLevel(planet: PlanetType, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(shipLevel = level) }

    fun withCargoLevel(planet: PlanetType, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(cargoLevel = level) }

    fun withColony(planet: PlanetType, level: Int, bonus: PlanetBonus): Galaxy =
            withChangedPlanet(planet) { it.copy(colonyLevel = level, colonyBonus = bonus) }

    fun withProject(project: Project) : Galaxy {
        val newPlanets = if (project.telescope != null) {
            unlockedPlanets + project.telescope.unlockedPlanets
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
        val oresAccessible = highestOre?.let { it in accessibleOreTypes } ?: true
        val alloysAccessible = highestAlloy?.let { it in accessibleAlloyTypes } ?: true
        val itemsAccessible = highestItem?.let { it in accessibleItemTypes } ?: true
        return oresAccessible && alloysAccessible && itemsAccessible
    }

    private val ResourceType.currentValue: Price
        get() = totalBonus.values.totalMultiplier[this]?.applyTo(baseValue) ?: baseValue

    private val Resources.totalValue: Price
        get() = resources.map { it.resourceType.currentValue * it.quantity }.sum()

    fun getApproximateTime(resources: Resources): Duration {
        val smeltTime = if (resources.hasAlloys) resources.totalSmeltTimeFromOre / nbSmelters else Duration.ZERO
        val craftTime = if (resources.hasItems) resources.totalCraftTimeFromOresAndAlloys/ nbCrafters else Duration.ZERO
        val reducedSmeltTime = totalBonus.production.smeltSpeed.applyAsSpeed(smeltTime)
        val reducedCraftTime = totalBonus.production.craftSpeed.applyAsSpeed(craftTime)
        return if (reducedSmeltTime > reducedCraftTime) reducedSmeltTime else reducedCraftTime
    }

    private fun getSmeltingIncome(alloyType: AlloyType): ValueRate {
        // TODO take into account number of smelters and limit recipes accordingly (offline VS online). For instance,
        //      bronze can only be smelted for a long time if we can also smelt copper and silver at the same time.
        val consumedValue = alloyType.requiredResources.totalValue
        val producedValue = alloyType.currentValue
        return (producedValue - consumedValue) / alloyType.smeltTime
    }

    private fun getCraftingIncome(itemType: ItemType): ValueRate {
        // TODO consider computing this value for offline crafting (see TODO in smelting income)
        val consumedValue = itemType.requiredResources.totalValue
        val producedValue = itemType.currentValue
        return (producedValue - consumedValue) / itemType.craftTime
    }

    private fun PlanetType.stateReport() = """
        $name:
            Stats:         ${planetStats[this]}
            Upgrade costs: ${planetCosts[this]}
    """.trimIndent()

    override fun toString(): String = PlanetType.values().joinToString("\n") { it.stateReport() }
}
