package org.hildan.ipm.helper

data class Recipe(
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val item: Sellable,
    val quantity: Int
)

enum class Project(
    val recipe: Recipe,
    val bonus: Bonus
) {
    ASTEROID_MINER(
        recipe = Recipe(
            listOf(
                Ingredient(OreType.COPPER, 400),
                Ingredient(OreType.IRON, 100)
            )
        ),
        bonus = NO_BONUS
    ),
    MANAGEMENT(
        recipe = Recipe(
            listOf(
                Ingredient(OreType.COPPER, 400),
                Ingredient(OreType.IRON, 50)
            )
        ),
        bonus = NO_BONUS
    )
}
