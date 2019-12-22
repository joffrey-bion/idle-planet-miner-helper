package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.children
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.sum
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.sumBy
import org.hildan.ipm.helper.utils.times
import org.hildan.ipm.helper.utils.completeEnumMap
import java.time.Duration
import java.util.EnumSet

data class Bonuses(
    val constant: ConstantBonuses,
    val researchedProjects: Set<Project> = EnumSet.noneOf(Project::class.java),
    val unlockedProjects: Set<Project> = EnumSet.of(Project.ASTEROID_MINER, Project.MANAGEMENT)
) {
    private val beaconActive = Project.BEACON in researchedProjects

    val oreTargetingActive = Project.ORE_TARGETING in researchedProjects

    val total = constant.total(beaconActive) + researchedProjects.map { it.bonus }.fold(Bonus.NONE, Bonus::plus)

    private val reducedSmeltIngredientsByAlloy: Map<AlloyType, Resources> =
            completeEnumMap { total.production.smeltIngredients.applyTo(it.requiredResources) }

    private val reducedCraftIngredientsByItem: Map<ItemType, Resources> =
            completeEnumMap { total.production.craftIngredients.applyTo(it.requiredResources) }

    private val smeltTimeFromOreByResourceType: Map<ResourceType, Duration> = computeRecursiveTimeByResource {
        total.production.smeltSpeed.applyAsSpeed(it.smeltTime)
    }

    private val craftTimeFromAlloysByResourceType: Map<ResourceType, Duration> = computeRecursiveTimeByResource {
        total.production.craftSpeed.applyAsSpeed(it.craftTime)
    }

    private fun computeRecursiveTimeByResource(selfTime: (ResourceType) -> Duration): Map<ResourceType, Duration> {
        val map = HashMap<ResourceType, Duration>()
        ResourceType.all().forEach { res ->
            map[res] = selfTime(res) + res.actualRequiredResources.resources.sumBy { cr ->
                map.getValue(cr.resourceType) * cr.quantity
            }
        }
        return map
    }

    private val ResourceType.actualRequiredResources: Resources
        get() = when(this) {
            is AlloyType -> actualRequiredResources
            is ItemType -> actualRequiredResources
            is OreType -> Resources.NOTHING
            else -> error("Unsupported resource type")
        }

    val AlloyType.actualRequiredResources: Resources
        get() = reducedSmeltIngredientsByAlloy.getValue(this)

    val ItemType.actualRequiredResources: Resources
        get() = reducedCraftIngredientsByItem.getValue(this)

    val ResourceType.actualSmeltTimeFromOre: Duration
        get() = smeltTimeFromOreByResourceType.getValue(this)

    val ResourceType.actualCraftTimeFromOresAndAlloys: Duration
        get() = craftTimeFromAlloysByResourceType.getValue(this)

    val ResourceType.currentValue: Price
        get() = total.values.totalMultiplier[this]?.applyTo(baseValue) ?: baseValue

    val Resources.totalValue: Price
        get() = resources.map { it.resourceType.currentValue * it.quantity }.sum()

    val Resources.totalSmeltTimeFromOre: Duration
        get() = resources.sumBy { it.resourceType.actualSmeltTimeFromOre * it.quantity }

    val Resources.totalCraftTimeFromOresAndAlloys: Duration
        get() = resources.sumBy { it.resourceType.actualCraftTimeFromOresAndAlloys * it.quantity }

    fun withProject(project: Project) : Bonuses = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children - project
    )
}
