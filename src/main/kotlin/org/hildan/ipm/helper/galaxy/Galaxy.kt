package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonuses
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.div
import org.hildan.ipm.helper.galaxy.money.sumRates
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.andBelow
import org.hildan.ipm.helper.utils.associateMerging
import org.hildan.ipm.helper.utils.div
import org.hildan.ipm.helper.utils.max
import org.hildan.ipm.helper.utils.sumBy
import java.time.Duration
import java.util.EnumSet

data class Galaxy private constructor(
    val bonuses: Bonuses,
    val currentCash: Price = Price(180),
    val planets: List<Planet> = emptyList(),
    val unlockedPlanets: Set<PlanetType> = TelescopeLevel(0).unlockedPlanets,
    val nbSmelters: Int = 0,
    val nbCrafters: Int = 0,
    val highestUnlockedAlloyRecipe: AlloyType = AlloyType.COPPER_BAR,
    val highestUnlockedItemRecipe: ItemType = ItemType.COPPER_WIRE
) {
    val planetStats = planets.associate { it.type to bonuses.total.forPlanet(it.type).applyTo(it.stats) }

    val planetCosts = planets.associate { it.type to bonuses.total.reduceUpgradeCosts(it.upgradeCosts, it.colonyLevel) }

    private val oreRatesByType: Map<OreType, Rate> = planets
        .flatMap { planetStats.getValue(it.type).deliveryRateByOreType(it, bonuses.oreTargetingActive) }
        .associateMerging({ it.oreType }, { it.rate }, Rate::plus)

    private val accessibleOres: Set<OreType>
            get() = oreRatesByType.keys

    private val accessibleAlloys: Set<AlloyType> = if (nbSmelters == 0) emptySet() else highestUnlockedAlloyRecipe
        .andBelow()
        .filterTo(EnumSet.noneOf(AlloyType::class.java)) {
            accessibleOres.containsAll(it.requiredResources.allOreTypes)
        }

    private val accessibleItems: Set<ItemType> = if (nbCrafters == 0) emptySet() else highestUnlockedItemRecipe
        .andBelow()
        .filterTo(EnumSet.noneOf(ItemType::class.java)) {
            accessibleAlloys.containsAll(it.requiredResources.allAlloyTypes)
        }

    private val accessibleResources: Set<ResourceType> =
            accessibleOres as Set<ResourceType> + accessibleAlloys + accessibleItems

    val maxIncomeSmeltRecipe: AlloyType? = with(bonuses) { accessibleAlloys.maxBy { it.smeltIncome } }

    val maxIncomeCraftRecipe: ItemType? = with(bonuses) { accessibleItems.maxBy { it.craftIncome } }

    val totalIncomeRate: ValueRate = with(bonuses) {
        val oreIncome = oreRatesByType.map { (oreType, rate) -> oreType.currentValue * rate }.sumRates()
        val smeltIncome = maxIncomeSmeltRecipe?.let { with(bonuses) { it.smeltIncome } } ?: ValueRate.ZERO
        val craftIncome = maxIncomeCraftRecipe?.let { with(bonuses) { it.craftIncome } } ?: ValueRate.ZERO
        oreIncome + smeltIncome + craftIncome
    }

    fun withBoughtPlanet(planet: PlanetType) : Galaxy = copy(
        planets = planets + Planet(planet),
        unlockedPlanets = unlockedPlanets - planet
    )

    private inline fun withChangedPlanet(planet: PlanetType, transform: (Planet) -> Planet) : Galaxy = copy(
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
            bonuses = bonuses.withProject(project),
            nbSmelters = newNbSmelters,
            nbCrafters = newNbCrafters
        )
    }

    fun withProjects(projects: List<Project>) : Galaxy {
        var nextGalaxy = this
        projects.forEach { nextGalaxy = nextGalaxy.withProject(it) }
        return nextGalaxy
    }

    fun Resources.areAccessible(): Boolean =
            accessibleResources.containsAll(allResourceTypes) && oreRatesByType.keys.containsAll(allOreTypes)

    private val Resources.dividedSmeltTimeFromOre: Duration
        get() = with(bonuses) { totalSmeltTimeFromOre / nbSmelters }

    private val Resources.dividedCraftTimeFromOresAndAlloys: Duration
        get() = with(bonuses) { totalCraftTimeFromOresAndAlloys / nbCrafters }

    fun getApproximateTime(resources: Resources): Duration {
        val ores = resources.resources.filterKeys { it is OreType }
        val oreGatheringTime = ores.entries.sumBy { (type, qty) -> qty / oreRatesByType.getValue(type as OreType) }
        val smeltTime = if (resources.hasAlloys) resources.dividedSmeltTimeFromOre else Duration.ZERO
        val craftTime = if (resources.hasItems) resources.dividedCraftTimeFromOresAndAlloys else Duration.ZERO
        return oreGatheringTime + max(smeltTime, craftTime)
    }

    private fun PlanetType.stateReport() = """
        $name:
            Stats:         ${planetStats[this]}
            Upgrade costs: ${planetCosts[this]}
    """.trimIndent()

    override fun toString(): String = PlanetType.values().joinToString("\n") { it.stateReport() }

    companion object {

        fun init(constantBonuses: ConstantBonuses): Galaxy = Galaxy(Bonuses(constantBonuses))
    }
}
