package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.OreType.*
import org.hildan.ipm.helper.galaxy.AlloyType.*
import org.hildan.ipm.helper.galaxy.Item.*

data class ProjectRecipe(
    val ingredients: List<Ingredient>
) {
    companion object {
        fun of(vararg ingredients: Ingredient) = ProjectRecipe(ingredients.toList())
    }
}

data class Ingredient(
    val item: Sellable,
    val quantity: Int
)

private infix fun Int.of(item: Sellable): Ingredient = Ingredient(item, this)

val Project.children: Set<Project> get() = ProjectGraph.children[this] ?: emptySet()
val Project.parents: Set<Project> get() = ProjectGraph.parents[this] ?: emptySet()

enum class Project(
    val recipe: ProjectRecipe,
    val bonus: Bonus = Bonus.NONE
) {
    /** Ability to mine asteroids */
    ASTEROID_MINER(ProjectRecipe.of(400 of COPPER, 100 of IRON)),
    /** Ability to assign managers */
    MANAGEMENT(ProjectRecipe.of(400 of COPPER, 50 of IRON)),
    SMELTER(ProjectRecipe.of(600 of COPPER, 250 of IRON)),
    TELESCOPE_1(ProjectRecipe.of(5 of COPPER_BAR, 1_500 of IRON)),
    CRAFTER(ProjectRecipe.of(5000 of LEAD, 5 of IRON_BAR)),
    BEACON(ProjectRecipe.of(15 of IRON_BAR)),
    ROVER(ProjectRecipe.of(10 of COPPER_WIRE)),

    ADVANCED_MINING(ProjectRecipe.of(5 of BATTERY, 20 of ALUMINUM_BAR), Bonus.allPlanets(mineRate = 1.25)),
    ADVANCED_THRUSTERS(ProjectRecipe.of(2 of GLASS, 10 of GOLD_BAR), Bonus.allPlanets(shipSpeed = 1.25)),
    ADVANCED_CARGO_HANDLING(ProjectRecipe.of(5 of HAMMER, 25 of SILVER_BAR), Bonus.allPlanets(cargo = 1.25)),
    ORE_TARGETING(ProjectRecipe.of(100 of HAMMER, 50 of BATTERY)),
    COLONY_TAX_INCENTIVES(
        recipe = ProjectRecipe.of(60 of ALUMINUM_BAR),
        bonus = Bonus(planetUpgradeCostMultiplierPerColonyLevel = Multiplier(0.95))
    ),
    COLONY_ADVANCED_TAX_INCENTIVES(
        recipe = ProjectRecipe.of(60 of BRONZE),
        bonus = Bonus(planetUpgradeCostMultiplierPerColonyLevel = Multiplier(0.95))
    )

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
