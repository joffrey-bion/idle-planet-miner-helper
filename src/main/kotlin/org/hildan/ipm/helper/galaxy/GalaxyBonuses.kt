package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.sum
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.LazyMap
import org.hildan.ipm.helper.utils.sumBy
import org.hildan.ipm.helper.utils.times
import org.hildan.ipm.helper.utils.lazyEnumMap
import java.time.Duration
import java.util.EnumSet

data class GalaxyBonuses(
    val constant: ConstantBonuses,
    val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT)
) {
    private val beaconActive = Project.BEACON in researchedProjects

    val oreTargetingActive = Project.ORE_TARGETING in researchedProjects

    val total = constant.total(beaconActive) + researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)

    private val reducedSmeltIngredientsByAlloy: Map<AlloyType, Resources> = lazyEnumMap {
        total.production.smeltIngredients.applyTo(it.requiredResources)
    }

    private val reducedCraftIngredientsByItem: Map<ItemType, Resources> = lazyEnumMap {
        total.production.craftIngredients.applyTo(it.requiredResources)
    }

    private val smeltTimeFromOreByResourceType: Map<ResourceType, Duration> = LazyMap { res, cache ->
        val selfTime = total.production.smeltSpeed.applyAsSpeed(res.smeltTime)
        selfTime + res.computeDependenciesDuration(cache)
    }

    private val craftTimeFromAlloysByResourceType: Map<ResourceType, Duration> = LazyMap { res, cache ->
        val selfTime = total.production.craftSpeed.applyAsSpeed(res.craftTime)
        selfTime + res.computeDependenciesDuration(cache)
    }

    private fun ResourceType.computeDependenciesDuration(
        cache: LazyMap<ResourceType, Duration>
    ): Duration = actualRequiredResources.resources.entries.sumBy { (type, qty) ->
        cache.getValue(type) * qty
    }

    // TODO consider offline recipes
    private val smeltingIncomeByAlloy: Map<AlloyType, ValueRate> = lazyEnumMap {
        val consumedValue = it.actualRequiredResources.totalValue
        val producedValue = it.currentValue
        (producedValue - consumedValue) / total.production.smeltSpeed.applyAsSpeed(it.smeltTime)
    }

    // TODO consider offline recipes
    private val craftingIncomeByItem: Map<ItemType, ValueRate> = lazyEnumMap {
        val consumedValue = it.actualRequiredResources.totalValue
        val producedValue = it.currentValue
        (producedValue - consumedValue) / total.production.craftSpeed.applyAsSpeed(it.craftTime)
    }

    private val ResourceType.actualRequiredResources: Resources
        get() = when(this) {
            is AlloyType -> actualRequiredResources
            is ItemType -> actualRequiredResources
            is OreType -> Resources.NOTHING
            else -> error("Unsupported resource type")
        }

    private val AlloyType.actualRequiredResources: Resources
        get() = reducedSmeltIngredientsByAlloy.getValue(this)

    private val ItemType.actualRequiredResources: Resources
        get() = reducedCraftIngredientsByItem.getValue(this)

    private val ResourceType.actualSmeltTimeFromOre: Duration
        get() = smeltTimeFromOreByResourceType.getValue(this)

    private val ResourceType.actualCraftTimeFromOresAndAlloys: Duration
        get() = craftTimeFromAlloysByResourceType.getValue(this)

    val AlloyType.smeltIncome: ValueRate
        get() = smeltingIncomeByAlloy.getValue(this)

    val ItemType.craftIncome: ValueRate
        get() = craftingIncomeByItem.getValue(this)

    val ResourceType.currentValue: Price
        get() = total.values.totalMultiplier[this]?.applyTo(baseValue) ?: baseValue

    private val Resources.totalValue: Price
        get() = resources.map { (type, qty) -> type.currentValue * qty }.sum()

    val Resources.totalSmeltTimeFromOre: Duration
        get() = resources.entries.sumBy { (type, qty) -> type.actualSmeltTimeFromOre * qty }

    val Resources.totalCraftTimeFromOresAndAlloys: Duration
        get() = resources.entries.sumBy { (type, qty) -> type.actualCraftTimeFromOresAndAlloys * qty }

    fun withProject(project: Project) : GalaxyBonuses = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children - project
    )
}
