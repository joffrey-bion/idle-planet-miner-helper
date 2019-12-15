package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.OreType.*
import org.hildan.ipm.helper.galaxy.resources.AlloyType.*
import org.hildan.ipm.helper.galaxy.resources.ItemType.*
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of

val Project.children: Set<Project> get() = ProjectGraph.children[this] ?: emptySet()
val Project.parents: Set<Project> get() = ProjectGraph.parents[this] ?: emptySet()

enum class Project(
    val requiredResources: Resources,
    val bonus: Bonus = Bonus.NONE
) {
    /** Ability to mine asteroids */
    ASTEROID_MINER(Resources.of(400 of COPPER, 100 of IRON)),
    /** Ability to assign managers */
    MANAGEMENT(Resources.of(400 of COPPER, 50 of IRON)),
    SMELTER(Resources.of(600 of COPPER, 250 of IRON)),
    TELESCOPE_1(Resources.of(5 of COPPER_BAR, 1_500 of IRON)),
    CRAFTER(Resources.of(5000 of LEAD, 5 of IRON_BAR)),
    BEACON(Resources.of(15 of IRON_BAR)),
    ROVER(Resources.of(10 of COPPER_WIRE)),

    ADVANCED_MINING(Resources.of(5 of BATTERY, 20 of ALUMINUM_BAR), Bonus.allPlanets(mineRate = 1.25)),
    ADVANCED_THRUSTERS(Resources.of(2 of GLASS, 10 of GOLD_BAR), Bonus.allPlanets(shipSpeed = 1.25)),
    ADVANCED_CARGO_HANDLING(Resources.of(5 of HAMMER, 25 of SILVER_BAR), Bonus.allPlanets(cargo = 1.25)),
    ORE_TARGETING(Resources.of(100 of HAMMER, 50 of BATTERY)),
    COLONY_TAX_INCENTIVES(Resources.of(60 of ALUMINUM_BAR), Bonus(planetUpgradeCost5pReductions = 1)),
    COLONY_ADVANCED_TAX_INCENTIVES(Resources.of(60 of BRONZE), Bonus(planetUpgradeCost5pReductions = 1))

    // TODO fill in all projects
}

private object ProjectGraph {

    val children: Map<Project, Set<Project>> = mapOf(
        Project.ASTEROID_MINER to setOf(Project.SMELTER, Project.ROVER),
        Project.MANAGEMENT to setOf(Project.TELESCOPE_1),// Project.COLONIZATION),
        Project.TELESCOPE_1 to setOf(Project.BEACON),// Project.TELESCOPE_2),
        Project.BEACON to setOf(),
        Project.SMELTER to setOf(Project.CRAFTER),// Project.ADVANCED_FURNACE),
        Project.CRAFTER to setOf(),// Project.ADVANCED_CRAFTER),
        Project.ADVANCED_MINING to setOf(Project.ADVANCED_THRUSTERS, Project.ADVANCED_CARGO_HANDLING),
        Project.ADVANCED_THRUSTERS to setOf(),// Project.SUPERIOR_MINING),
        Project.ADVANCED_CARGO_HANDLING to setOf()//, Project.SUPERIOR_MINING),

        // TODO fill in other project dependencies
    )

    val parents: Map<Project, Set<Project>> = Project.values().toList()
        .associateWith { child -> children.filterValues { child in it }.keys }
}
