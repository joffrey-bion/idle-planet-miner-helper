package org.hildan.ipm.helper

import org.hildan.ipm.helper.OreType.*
import org.hildan.ipm.helper.AlloyType.*
import org.hildan.ipm.helper.Item.*

data class ProjectRecipe(
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val item: Sellable,
    val quantity: Int
)

val Project.children: Set<Project> get() = ProjectGraph.children[this] ?: emptySet()
val Project.parents: Set<Project> get() = ProjectGraph.parents[this] ?: emptySet()

enum class Project(
    val recipe: ProjectRecipe,
    val bonus: Bonus
) {
    ASTEROID_MINER(
        recipe = ProjectRecipe(
            listOf(
                Ingredient(COPPER, 400),
                Ingredient(IRON, 100)
            )
        ),
        bonus = Bonus.NONE // ability to mine asteroids
    ),
    MANAGEMENT(
        recipe = ProjectRecipe(
            listOf(
                Ingredient(COPPER, 400),
                Ingredient(IRON, 50)
            )
        ),
        bonus = Bonus.NONE // ability to assign managers
    ),
    TELESCOPE_1(
        recipe = ProjectRecipe(
            listOf(
                Ingredient(COPPER_BAR, 5),
                Ingredient(IRON, 1_500)
            )
        ),
        bonus = Bonus.NONE // ability to assign managers
    ),
    BEACON(
        recipe = ProjectRecipe(
            listOf(
                Ingredient(IRON_BAR, 15)
            )
        ),
        bonus = Bonus.NONE // beacon bonus is calculated separately
    ),
    ORE_TARGETING(
        recipe = ProjectRecipe(
            listOf(
                Ingredient(HAMMER, 100),
                Ingredient(BATTERY, 50)
            )
        ),
        bonus = Bonus.NONE // calculated separately
    )

    // TODO fill in all projects
}

private object ProjectGraph {

    val children: Map<Project, Set<Project>> = mapOf(
        Project.ASTEROID_MINER to setOf(), // TODO add SMELTER as child
        Project.MANAGEMENT to setOf(Project.TELESCOPE_1),
        Project.TELESCOPE_1 to setOf(Project.BEACON),
        Project.BEACON to setOf()

        // TODO fill in other project dependencies
    )

    val parents: Map<Project, Set<Project>> = Project.values().toList()
        .associateWith { child -> children.filterValues { child in it }.keys }
}
