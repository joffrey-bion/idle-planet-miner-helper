package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.children
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.sum
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.EMap
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
            EMap.of { total.production.smeltIngredients.applyTo(it.requiredResources) }

    private val reducedCraftIngredientsByItem: Map<ItemType, Resources> =
            EMap.of { total.production.craftIngredients.applyTo(it.requiredResources) }

    val AlloyType.actualRequiredResources: Resources
        get() = reducedSmeltIngredientsByAlloy.getValue(this)

    val ItemType.actualRequiredResources: Resources
        get() = reducedCraftIngredientsByItem.getValue(this)

    val ResourceType.currentValue: Price
        get() = total.values.totalMultiplier[this]?.applyTo(baseValue) ?: baseValue

    val Resources.totalValue: Price
        get() = resources.map { it.resourceType.currentValue * it.quantity }.sum()

    fun withProject(project: Project) : Bonuses = copy(
        researchedProjects = researchedProjects + project,
        unlockedProjects = unlockedProjects + project.children - project
    )
}
