package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.resources.OreType.*
import org.hildan.ipm.helper.galaxy.resources.AlloyType.*
import org.hildan.ipm.helper.galaxy.resources.ItemType.*
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import org.hildan.ipm.helper.utils.associateMerging

val Project.children: Set<Project> get() = ProjectGraph.children[this] ?: emptySet()

@JvmInline
value class TelescopeLevel(private val value: Int) {

    val unlockedPlanets: Set<Planet>
        get() = unlockedPlanetsByTelescopeLevel.getValue(this)
}

private val unlockedPlanetsByTelescopeLevel: Map<TelescopeLevel, Set<Planet>> =
        Planet.entries.associateMerging({ it.telescopeLevel }, { setOf(it) }, { s1, s2 -> s1 + s2 })

enum class Project(
    val requiredResources: Resources,
    val bonus: Bonus = Bonus.NONE,
    val telescope: TelescopeLevel? = null
) {
    /** Ability to mine asteroids */
    ASTEROID_MINER(Resources.of(400 of COPPER, 100 of IRON)),
    /** Ability to assign managers */
    MANAGEMENT(Resources.of(400 of COPPER, 50 of IRON)),
    BEACON(Resources.of(15 of IRON_BAR)),
    ROVER(Resources.of(10 of COPPER_WIRE)),
    CARGO_LOGISTICS(Resources.of(10 of ALUMINUM_BAR, 3 of CIRCUIT)),
    ORE_TARGETING(Resources.of(100 of HAMMER, 50 of BATTERY)),

    SMELTER(Resources.of(600 of COPPER, 250 of IRON)),
    CRAFTER(Resources.of(5000 of LEAD, 5 of IRON_BAR)),

    TELESCOPE_1(Resources.of(5 of COPPER_BAR, 1_500 of IRON), telescope = TelescopeLevel(1)),
    TELESCOPE_2(Resources.of(10 of LEAD_BAR, 500 of SILICA), telescope = TelescopeLevel(2)),
    TELESCOPE_3(Resources.of(10 of IRON_NAIL, 15 of SILICON_BAR), telescope = TelescopeLevel(3)),
    TELESCOPE_4(Resources.of(5 of HAMMER, 20 of ALUMINUM_BAR), telescope = TelescopeLevel(4)),
    TELESCOPE_5(Resources.of(3 of CIRCUIT, 10 of GOLD_BAR), telescope = TelescopeLevel(5)),
    TELESCOPE_6(Resources.of(3 of LASER, 25 of BRONZE_BAR), telescope = TelescopeLevel(6)),
    TELESCOPE_7(Resources.of(3 of SOLAR_PANEL, 20 of PLATINUM), telescope = TelescopeLevel(7)),

    ADVANCED_MINING(Resources.of(5 of BATTERY, 20 of ALUMINUM_BAR), Bonus.allPlanets(mineRate = 1.25)),
    ADVANCED_THRUSTERS(Resources.of(2 of GLASS, 10 of GOLD_BAR), Bonus.allPlanets(shipSpeed = 1.25)),
    ADVANCED_CARGO_HANDLING(Resources.of(5 of HAMMER, 25 of SILVER_BAR), Bonus.allPlanets(cargo = 1.25)),
    SUPERIOR_MINING(Resources.of(10 of LASER_TORCH, 25 of PLATINUM_BAR), Bonus.allPlanets(mineRate = 1.25)),
    SUPERIOR_THRUSTERS(Resources.of(4 of ADVANCED_BATTERY), Bonus.allPlanets(shipSpeed = 1.25)),
    SUPERIOR_CARGO_HANDLING(Resources.of(50 of TITANIUM_BAR), Bonus.allPlanets(cargo = 1.25)),

    COLONIZATION(Resources.of(20 of COPPER_BAR, 10 of IRON_BAR)),
    COLONIZATION_SCOUTING(Resources.of(15 of IRON_NAIL)),
    COLONY_TAX_INCENTIVES(Resources.of(60 of ALUMINUM_BAR), Bonus(planetUpgradeCost5pReductions = 1)),
    COLONY_ADVANCED_TAX_INCENTIVES(Resources.of(60 of BRONZE_BAR), Bonus(planetUpgradeCost5pReductions = 1)),
    COLONY_SUPERIOR_TAX_INCENTIVES(Resources.of(60 of PALADIUM_BAR), Bonus(planetUpgradeCost5pReductions = 1)),

    ADVANCED_FURNACE(Resources.of(3 of GLASS, 10 of ALUMINUM_BAR), Bonus.production(smeltSpeed = 1.2)),
    ADVANCED_CRAFTER(Resources.of(5 of LENSE, 50 of GOLD_BAR), Bonus.production(craftSpeed = 1.2)),
    ADVANCED_ALLOY_VALUE(Resources.of(3 of CIRCUIT, 10 of SILVER_BAR), Bonus.values(alloysMultiplier = 1.2)),
    //    SUPERIOR_ALLOY_VALUE(Resources.of(...), Bonus.values(alloysMultiplier = 1.2)),
    ADVANCED_ITEM_VALUE(Resources.of(1 of LENSE, 5 of SILVER_BAR), Bonus.values(itemsMultiplier = 1.2)),
    SMELTING_EFFICIENCY(Resources.of(200 of BRONZE_BAR), Bonus.production(smeltIngredients = 0.8)),
    CRAFTING_EFFICIENCY(Resources.of(40 of SOLAR_PANEL), Bonus.production(craftIngredients = 0.8)),
    SUPERIOR_FURNACE(Resources.of(5 of IRIDIUM_BAR, 20 of TITANIUM_BAR, 50 of PLATINUM_BAR), Bonus.production(smeltSpeed = 1.2)),
    SUPERIOR_CRAFTING(Resources.of(2 of THERMAL_SCANNER, 10 of ADVANCED_BATTERY, 20 of LASER_TORCH), Bonus.production(craftSpeed = 1.2)),
    // TODO fill in all projects
}

private object ProjectGraph {

    val children: Map<Project, Set<Project>> = mapOf(
        Project.ASTEROID_MINER to setOf(Project.SMELTER, Project.ROVER),
        Project.ROVER to setOf(Project.ADVANCED_MINING),
        Project.ADVANCED_MINING to setOf(Project.ADVANCED_THRUSTERS, Project.ADVANCED_CARGO_HANDLING),
        Project.ADVANCED_THRUSTERS to setOf(Project.SUPERIOR_MINING),
        Project.ADVANCED_CARGO_HANDLING to setOf(Project.SUPERIOR_MINING),
        Project.SUPERIOR_MINING to setOf(Project.SUPERIOR_THRUSTERS, Project.SUPERIOR_CARGO_HANDLING),

        Project.SMELTER to setOf(Project.CRAFTER, Project.ADVANCED_FURNACE),
        Project.CRAFTER to setOf(Project.ADVANCED_CRAFTER),
        Project.ADVANCED_FURNACE to setOf(Project.SMELTING_EFFICIENCY, Project.ADVANCED_ALLOY_VALUE),
        Project.ADVANCED_CRAFTER to setOf(Project.CRAFTING_EFFICIENCY, Project.ADVANCED_ITEM_VALUE),
        Project.SMELTING_EFFICIENCY to setOf(Project.SUPERIOR_FURNACE),
        Project.CRAFTING_EFFICIENCY to setOf(Project.SUPERIOR_CRAFTING),
        Project.SUPERIOR_FURNACE to setOf(), // PREFERRED_VENDOR, FURNACE_OVERDRIVE
        Project.SUPERIOR_CRAFTING to setOf(),

        Project.MANAGEMENT to setOf(Project.TELESCOPE_1, Project.COLONIZATION),
        Project.TELESCOPE_1 to setOf(Project.BEACON, Project.TELESCOPE_2),
        Project.BEACON to setOf(),
        Project.TELESCOPE_2 to setOf(Project.TELESCOPE_3), // + resource details
        Project.TELESCOPE_3 to setOf(Project.TELESCOPE_4),
        Project.TELESCOPE_4 to setOf(Project.TELESCOPE_5, Project.CARGO_LOGISTICS),
        Project.CARGO_LOGISTICS to setOf(Project.ORE_TARGETING),
        Project.TELESCOPE_5 to setOf(Project.TELESCOPE_6),
        Project.TELESCOPE_6 to setOf(Project.TELESCOPE_7),

        Project.COLONIZATION to setOf(Project.COLONIZATION_SCOUTING), // COLONIZATION_EFFICIENCY
        Project.COLONIZATION_SCOUTING to setOf(Project.COLONY_TAX_INCENTIVES),
        Project.COLONY_TAX_INCENTIVES to setOf(Project.COLONY_ADVANCED_TAX_INCENTIVES),
        Project.COLONY_ADVANCED_TAX_INCENTIVES to setOf(Project.COLONY_SUPERIOR_TAX_INCENTIVES)

        // TODO fill in other project dependencies
    )
}
