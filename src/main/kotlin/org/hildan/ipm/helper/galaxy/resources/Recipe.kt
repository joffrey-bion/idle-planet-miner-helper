package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.Price

infix fun Int.of(resource: Resource): Ingredient = Ingredient(resource, this)

data class Recipe(
    val ingredients: List<Ingredient>
) {
    private val resources: Set<Resource> get() = ingredients.map { it.resource }.toSet()

    val highestOre: OreType? =
            (resources.mapNotNull { it.recipe.highestOre } + resources.filterIsInstance<OreType>()).max()

    val highestAlloy: AlloyType? =
            (resources.mapNotNull { it.recipe.highestAlloy } + resources.filterIsInstance<AlloyType>()).max()

    val highestItem: ItemType? =
            (resources.mapNotNull { it.recipe.highestItem } + resources.filterIsInstance<ItemType>()).max()

    companion object {
        val NOTHING = Recipe(emptyList())

        fun of(vararg ingredients: Ingredient): Recipe = Recipe(ingredients.toList())
    }
}

data class Ingredient(
    val resource: Resource,
    val quantity: Int
)

interface Resource {
    val baseValue: Price
    val recipe: Recipe

    companion object {

        fun all(): List<Resource> = emptyList<Resource>() + OreType.values() + AlloyType.values() + ItemType.values()
    }
}
