package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.GalaxyBonuses
import org.hildan.ipm.helper.galaxy.money.*
import org.hildan.ipm.helper.utils.*
import kotlin.math.*
import kotlin.math.min

object Smelters {

    const val MAX = 9

    private val smelterPrices = mapOf(
        // first one can't be bought
        2 to Price(50.k()),
        3 to Price(500.k()),
        4 to Price(10.M()),
        5 to Price(5.B()),
        6 to Price(100.B()),
        7 to Price(50.T()),
        8 to Price(500.T()),
        9 to Price(250.q())
    )

    fun priceForOneMore(currentNumber: Int): Price = smelterPrices.getValue(currentNumber + 1)
}

object Crafters {

    const val MAX = 8

    private val crafterPrices = mapOf(
        // first one can't be bought
        2 to Price(1.M()),
        3 to Price(100.M()),
        4 to Price(50.B()),
        5 to Price(1.T()),
        6 to Price(500.T()),
        7 to Price(5.q()),
        8 to Price(2500.q())
    )

    fun priceForOneMore(currentNumber: Int): Price = crafterPrices.getValue(currentNumber + 1)
}

fun optimalRecipesFor(
    unlockedResources: Set<ResourceType>,
    nSmelters: Int,
    nCrafters: Int,
    galaxyBonuses: GalaxyBonuses,
): SmeltersCraftersSetup {
    val recipesSetup = RecipeOptimizer(unlockedResources, nSmelters, nCrafters, galaxyBonuses).optimize()
    val recipesList = recipesSetup.activeRecipes.flatMap { (t, q) -> List(q) { t } }
    return SmeltersCraftersSetup(
        smelters = recipesList.filterIsInstance<AlloyType>().sorted(),
        crafters = recipesList.filterIsInstance<ItemType>().sorted(),
        totalIncome = recipesSetup.totalIncome,
    )
}

private class RecipeOptimizer(
    unlockedResources: Set<ResourceType>,
    private var unusedSmelters: Int,
    private var unusedCrafters: Int,
    private val galaxyBonuses: GalaxyBonuses,
) {
    private val alloys = unlockedResources.asSequence().filterIsInstance<AlloyType>().sorted().toCollection(ArrayDeque())
    private val items = unlockedResources.asSequence().filterIsInstance<ItemType>().sorted().toCollection(ArrayDeque())

    private val activeRecipes: MutableMap<ResourceType, Int> = mutableMapOf()
    private val production: MutableMap<ResourceType, Rate> = mutableMapOf()

    private var bestSetupSoFar: RecipesSetup? = null

    fun optimize(): RecipesSetup {
        computeOneMoreResourceType()
        return bestSetupSoFar ?: error("No recipes setup found (this should never happen)")
    }

    private fun computeOneMoreResourceType() {
        // we need to accept leaving some crafters unused, because it may be useless to add more
        if (unusedSmelters == 0 && (unusedCrafters == 0 || items.isEmpty())) {
            updateBestSolutionIfNeeded()
            return
        }
        // we want to fill up smelters first, to enable more crafts
        if (alloys.isNotEmpty() && unusedSmelters > 0) {
            val alloy = alloys.removeFirst()
            computeWithNRecipesOf(alloy, unusedSmelters)
            alloys.addFirst(alloy)
        } else if (items.isNotEmpty() && unusedCrafters > 0) {
            val item = items.removeFirst()
            computeWithNRecipesOf(item, unusedCrafters)
            items.addFirst(item)
        }
    }

    private fun updateBestSolutionIfNeeded() {
        val income = with(galaxyBonuses) {
            production.map { (type, qty) -> type.currentValue * qty }.sumRates()
        }
        if (bestSetupSoFar == null || income > bestSetupSoFar!!.totalIncome) {
            bestSetupSoFar = RecipesSetup(
                activeRecipes = activeRecipes.filterValues { it > 0 }, // conveniently creates a new map as well
                totalIncome = income,
            )
        }
    }

    private fun computeWithNRecipesOf(resource: ResourceType, smeltersCraftersCap: Int) {
        computeOneMoreResourceType() // considers the option of not producing this resource at all

        val maxParallelProduction = min(smeltersCraftersCap.toDouble(), maxParallelProduction(resource))
        var nRecipes = 1.0
        while (nRecipes <= maxParallelProduction) {
            updateActiveRecipes(resource, nRecipes)
            computeOneMoreResourceType()
            updateActiveRecipes(resource, -nRecipes)
            nRecipes += 1.0
        }
        // one more run for non integer part
        if (maxParallelProduction < 1.0 || nRecipes < maxParallelProduction) {
            updateActiveRecipes(resource, maxParallelProduction)
            computeOneMoreResourceType()
            updateActiveRecipes(resource, -maxParallelProduction)
        }
    }

    /**
     * Gets the number of recipes of this resource type that we can activate in parallel given the current production.
     *
     * This is not a whole number because one smelter/crafter could be suboptimal (e.g. 1 copper bar produced every 20s,
     * but copper wire requires 5 every 60s, thus the copper wire crafter runs at 3/5th of its efficiency, but
     * this can still be attempted)
     */
    private fun maxParallelProduction(item: ResourceType): Double = with(galaxyBonuses) {
        val actualProductionTime = item.actualProductionTime
        item.actualRequiredResources.quantitiesByType.filterKeys { it !is OreType }.minOfOrNull { (t, q) ->
            ((production.getOrDefault(t, Rate.ZERO) * actualProductionTime) / q)
        } ?: Double.MAX_VALUE
    }

    private fun updateActiveRecipes(resource: ResourceType, qty: Double) {
        val requiredRecipeQty = qty.makeWhole()
        when (resource) {
            is ItemType -> unusedCrafters -= requiredRecipeQty
            is AlloyType -> unusedSmelters -= requiredRecipeQty
            else -> error("Unexpected resource type in recipe computation, got $resource")
        }
        activeRecipes.merge(resource, requiredRecipeQty, Int::plus)
        with(galaxyBonuses) {
            val productionTime = resource.actualProductionTime
            // we also count ores here because we want to count them as negative income
            resource.actualRequiredResources.quantitiesByType.forEach { (t, q) ->
                production.merge(t, (-q * qty) / productionTime, Rate::plus)
            }
            production.merge(resource, qty / productionTime, Rate::plus)
        }
    }
}

private data class RecipesSetup(
    val activeRecipes: Map<ResourceType, Int>,
    val totalIncome: ValueRate,
)

data class SmeltersCraftersSetup(
    val smelters: List<AlloyType>,
    val crafters: List<ItemType>,
    val totalIncome: ValueRate,
)

private fun Double.makeWhole(): Int = when {
    this < 0 -> floor(this).toInt()
    else -> ceil(this).toInt()
}