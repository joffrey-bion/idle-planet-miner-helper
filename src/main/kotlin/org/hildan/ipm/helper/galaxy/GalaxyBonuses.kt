package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.*
import org.hildan.ipm.helper.utils.LazyMap
import org.hildan.ipm.helper.utils.lazyEnumMap
import org.hildan.ipm.helper.utils.sumBy
import java.util.*
import kotlin.time.Duration

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

    private val reducedSmeltTimeByAlloy: Map<AlloyType, Duration> = lazyEnumMap {
        total.production.smeltSpeed.applyAsSpeed(it.smeltTime)
    }

    private val reducedCraftTimeByItem: Map<ItemType, Duration> = lazyEnumMap {
        total.production.craftSpeed.applyAsSpeed(it.craftTime)
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
    ): Duration = actualRequiredResources.quantitiesByType.entries.sumBy { (type, qty) ->
        cache.getValue(type) * qty
    }

    val ResourceType.actualRequiredResources: Resources
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

    val ResourceType.actualProductionTime: Duration
        get() = when (this) {
            is AlloyType -> reducedSmeltTimeByAlloy.getValue(this)
            is ItemType -> reducedCraftTimeByItem.getValue(this)
            is OreType -> Duration.ZERO
            else -> error("Unsupported resource type")
        }

    val ResourceType.currentValue: Price
        get() = total.values.totalMultiplier[this]?.applyTo(baseValue) ?: baseValue

    val Resources.totalSmeltTimeFromOre: Duration
        get() = quantitiesByType.entries.sumBy { (type, qty) -> type.actualSmeltTimeFromOre * qty }

    val Resources.totalCraftTimeFromOresAndAlloys: Duration
        get() = quantitiesByType.entries.sumBy { (type, qty) -> type.actualCraftTimeFromOresAndAlloys * qty }

    private val ResourceType.actualSmeltTimeFromOre: Duration
        get() = smeltTimeFromOreByResourceType.getValue(this)

    private val ResourceType.actualCraftTimeFromOresAndAlloys: Duration
        get() = craftTimeFromAlloysByResourceType.getValue(this)

    fun withProject(project: Project) : GalaxyBonuses = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children - project
    )
}
