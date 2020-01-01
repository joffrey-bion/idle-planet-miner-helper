package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.div
import org.hildan.ipm.helper.galaxy.money.sumRates
import org.hildan.ipm.helper.galaxy.planets.PlanetState
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.planets.Planets
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.andBelow
import org.hildan.ipm.helper.utils.div
import org.hildan.ipm.helper.utils.max
import org.hildan.ipm.helper.utils.sumBy
import java.time.Duration
import java.util.EnumSet

data class Galaxy private constructor(
    val bonuses: GalaxyBonuses,
    val currentCash: Price = Price(180),
    val planets: Planets = Planets(),
    val unlockedPlanets: Set<Planet> = TelescopeLevel(0).unlockedPlanets,
    val nbSmelters: Int = 0,
    val nbCrafters: Int = 0,
    val highestUnlockedAlloyRecipe: AlloyType = AlloyType.COPPER_BAR,
    val highestUnlockedItemRecipe: ItemType = ItemType.COPPER_WIRE
) {
    private val accessibleAlloys: Set<AlloyType> = if (nbSmelters == 0) emptySet() else highestUnlockedAlloyRecipe
        .andBelow()
        .filterTo(EnumSet.noneOf(AlloyType::class.java)) {
            planets.accessibleOres.containsAll(it.requiredResources.allOreTypes)
        }

    private val accessibleItems: Set<ItemType> = if (nbCrafters == 0) emptySet() else highestUnlockedItemRecipe
        .andBelow()
        .filterTo(EnumSet.noneOf(ItemType::class.java)) {
            accessibleAlloys.containsAll(it.requiredResources.allAlloyTypes)
        }

    private val accessibleResources: Set<ResourceType> = HashSet<ResourceType>().apply {
        addAll(planets.accessibleOres)
        addAll(accessibleAlloys)
        addAll(accessibleItems)
    }

    val maxIncomeSmeltRecipe: AlloyType? = with(bonuses) { accessibleAlloys.maxBy { it.smeltIncome } }

    val maxIncomeCraftRecipe: ItemType? = with(bonuses) { accessibleItems.maxBy { it.craftIncome } }

    val totalIncomeRate: ValueRate = with(bonuses) {
        val oreIncome = planets.oreRatesByType.map { (oreType, rate) -> oreType.currentValue * rate }.sumRates()
        val smeltIncome = maxIncomeSmeltRecipe?.smeltIncome ?: ValueRate.ZERO
        val craftIncome = maxIncomeCraftRecipe?.craftIncome ?: ValueRate.ZERO
        oreIncome + smeltIncome + craftIncome
    }

    fun withBoughtPlanet(planet: Planet) : Galaxy = copy(
        planets = planets.withBoughtPlanet(planet, bonuses),
        unlockedPlanets = unlockedPlanets - planet
    )

    private inline fun withChangedPlanet(planet: Planet, transform: (PlanetState) -> PlanetState) : Galaxy = copy(
        planets = planets.withChangedPlanet(planet, transform)
    )

    fun withLevels(planet: Planet, mine: Int, ships: Int, cargo: Int): Galaxy =
            withChangedPlanet(planet) { it.copy(mineLevel = mine, shipLevel = ships, cargoLevel = cargo) }

    fun withMineLevel(planet: Planet, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(mineLevel = level) }

    fun withShipLevel(planet: Planet, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(shipLevel = level) }

    fun withCargoLevel(planet: Planet, level: Int): Galaxy = withChangedPlanet(planet) { it.copy(cargoLevel = level) }

    fun withColony(planet: Planet, level: Int, bonus: PlanetBonus): Galaxy =
            withChangedPlanet(planet) { it.copy(colonyLevel = level, colonyBonus = bonus) }

    fun withProject(project: Project) : Galaxy {
        val newBonuses = bonuses.withProject(project)
        val newUnlockedPlanets = if (project.telescope != null) {
            unlockedPlanets + project.telescope.unlockedPlanets
        } else {
            unlockedPlanets
        }
        val newBPlanets = planets.withBonuses(newBonuses)
        val newNbSmelters = if (project == Project.SMELTER) 1 else nbSmelters
        val newNbCrafters = if (project == Project.CRAFTER) 1 else nbCrafters
        return copy(
            bonuses = newBonuses,
            unlockedPlanets = newUnlockedPlanets,
            planets = newBPlanets,
            nbSmelters = newNbSmelters,
            nbCrafters = newNbCrafters
        )
    }

    fun withProjects(projects: List<Project>) : Galaxy {
        var nextGalaxy = this
        projects.forEach { nextGalaxy = nextGalaxy.withProject(it) }
        return nextGalaxy
    }

    fun Resources.areAccessible(): Boolean = accessibleResources.containsAll(allResourceTypes)

    private val Resources.dividedSmeltTimeFromOre: Duration
        get() = with(bonuses) { totalSmeltTimeFromOre / nbSmelters }

    private val Resources.dividedCraftTimeFromOresAndAlloys: Duration
        get() = with(bonuses) { totalCraftTimeFromOresAndAlloys / nbCrafters }

    fun getApproximateTime(resources: Resources): Duration {
        val ores = resources.resources.filterKeys { it is OreType }
        val oreGatheringTime = ores.entries.sumBy { (type, qty) -> qty / planets.oreRatesByType.getValue(type as OreType) }
        val smeltTime = if (resources.hasAlloys) resources.dividedSmeltTimeFromOre else Duration.ZERO
        val craftTime = if (resources.hasItems) resources.dividedCraftTimeFromOresAndAlloys else Duration.ZERO
        return oreGatheringTime + max(smeltTime, craftTime)
    }

    private fun Planet.stateReport() = """
        $name:
            Stats:         ${planets.production[this]}
            Upgrade costs: ${planets.upgradeCosts[this]}
    """.trimIndent()

    override fun toString(): String = Planet.values().joinToString("\n") { it.stateReport() }

    companion object {

        fun init(constantBonuses: ConstantBonuses): Galaxy = Galaxy(GalaxyBonuses(constantBonuses))
    }
}
