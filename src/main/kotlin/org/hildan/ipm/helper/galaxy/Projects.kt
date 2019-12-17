package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.OreType.*
import org.hildan.ipm.helper.galaxy.resources.AlloyType.*
import org.hildan.ipm.helper.galaxy.resources.ItemType.*
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import java.util.EnumSet

val Project.children: Set<Project> get() = ProjectGraph.children[this] ?: emptySet()

inline class TelescopeLevel(val value: Int) {

    val unlockedPlanets: Set<PlanetType> get() = PlanetType.values()
        .filterTo(EnumSet.noneOf(PlanetType::class.java)) { it.telescopeLevel == this }
}

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
    TELESCOPE_2(Resources.of(10 of LEAD_BAR, 500 of SILICON), telescope = TelescopeLevel(2)),
    TELESCOPE_3(Resources.of(10 of IRON_NAIL, 15 of SILICON_BAR), telescope = TelescopeLevel(3)),
    TELESCOPE_4(Resources.of(5 of HAMMER, 20 of ALUMINUM_BAR), telescope = TelescopeLevel(4)),
    TELESCOPE_5(Resources.of(3 of CIRCUIT, 10 of GOLD_BAR), telescope = TelescopeLevel(5)),
    TELESCOPE_6(Resources.of(3 of LASER, 25 of BRONZE), telescope = TelescopeLevel(6)),
    TELESCOPE_7(Resources.of(3 of SOLAR_PANEL, 20 of PLATINUM), telescope = TelescopeLevel(7)),

    ADVANCED_MINING(Resources.of(5 of BATTERY, 20 of ALUMINUM_BAR), Bonus.allPlanets(mineRate = 1.25)),
    ADVANCED_THRUSTERS(Resources.of(2 of GLASS, 10 of GOLD_BAR), Bonus.allPlanets(shipSpeed = 1.25)),
    ADVANCED_CARGO_HANDLING(Resources.of(5 of HAMMER, 25 of SILVER_BAR), Bonus.allPlanets(cargo = 1.25)),

    COLONY_TAX_INCENTIVES(Resources.of(60 of ALUMINUM_BAR),
        Bonus(planetUpgradeCost5pReductions = 1)
    ),
    COLONY_ADVANCED_TAX_INCENTIVES(Resources.of(60 of BRONZE),
        Bonus(planetUpgradeCost5pReductions = 1)
    ),

    ADVANCED_FURNACE(Resources.of(3 of GLASS, 10 of ALUMINUM_BAR), Bonus.production(smeltSpeed = 1.2)),
    ADVANCED_CRAFTER(Resources.of(5 of LENSE, 50 of GOLD_BAR), Bonus.production(craftSpeed = 1.2)),
//    SMELTING_EFFICIENCY(Resources.of(200 of BRONZE), Bonus.production(smeltIngredients = 0.8))
//    CRAFTING_EFFICIENCY(Resources.of(40 of SOLAR_PANEL), Bonus.production(craftIngredients = 0.8))
    // TODO fill in all projects
}

private object ProjectGraph {

    val children: Map<Project, Set<Project>> = mapOf(
        Project.ASTEROID_MINER to setOf(Project.SMELTER, Project.ROVER),
        Project.MANAGEMENT to setOf(Project.TELESCOPE_1),// Project.COLONIZATION),
        Project.TELESCOPE_1 to setOf(Project.BEACON, Project.TELESCOPE_2),
        Project.BEACON to setOf(),
        Project.TELESCOPE_2 to setOf(Project.TELESCOPE_3), // + resource details
        Project.TELESCOPE_3 to setOf(Project.TELESCOPE_4),
        Project.TELESCOPE_4 to setOf(Project.TELESCOPE_5, Project.CARGO_LOGISTICS),
        Project.CARGO_LOGISTICS to setOf(Project.ORE_TARGETING),
        Project.TELESCOPE_5 to setOf(Project.TELESCOPE_6),
        Project.TELESCOPE_6 to setOf(Project.TELESCOPE_7),
        Project.SMELTER to setOf(Project.CRAFTER, Project.ADVANCED_FURNACE),
        Project.CRAFTER to setOf(Project.ADVANCED_CRAFTER),
        Project.ADVANCED_MINING to setOf(Project.ADVANCED_THRUSTERS, Project.ADVANCED_CARGO_HANDLING),
        Project.ADVANCED_THRUSTERS to setOf(),// Project.SUPERIOR_MINING),
        Project.ADVANCED_CARGO_HANDLING to setOf()//, Project.SUPERIOR_MINING),

        // TODO fill in other project dependencies
    )

    val parents: Map<Project, Set<Project>> = Project.values().toList()
        .associateWith { child -> children.filterValues { child in it }.keys }
}
