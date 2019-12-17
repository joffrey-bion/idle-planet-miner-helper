package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import java.time.Duration

infix fun Int.of(resourceType: ResourceType): CountedResource = CountedResource(resourceType, this)

interface ResourceType {
    val baseValue: Price
    val requiredResources: Resources
    val smeltTime: Duration
    val craftTime: Duration

    val smeltTimeFromOre: Duration
        get() = requiredResources.totalSmeltTimeFromOre + smeltTime
    val craftTimeFromOresAndAlloys: Duration
        get() = requiredResources.totalCraftTimeFromOresAndAlloys + craftTime

    companion object {
        fun all(): List<ResourceType> = emptyList<ResourceType>() + OreType.values() + AlloyType.values() + ItemType.values()
    }
}

data class Resources(
    val resources: List<CountedResource>
) {
    private val resourceTypes: Set<ResourceType>
        get() = resources.map { it.resourceType }.toSet()

    private val allResourceTypes: Set<ResourceType>
        get() = resourceTypes.flatMap { it.requiredResources.allResourceTypes + it }.toSet()

    val hasAlloys = allResourceTypes.any { it is AlloyType }

    val hasItems = allResourceTypes.any { it is ItemType }

    val highestOre: OreType? = allResourceTypes.filterIsInstance<OreType>().max()

    val highestAlloy: AlloyType? = allResourceTypes.filterIsInstance<AlloyType>().max()

    val highestItem: ItemType? = allResourceTypes.filterIsInstance<ItemType>().max()

    val totalSmeltTimeFromOre: Duration =
            resources.map { it.resourceType.smeltTimeFromOre * it.quantity }.sum()

    val totalCraftTimeFromOresAndAlloys: Duration =
            resources.map { it.resourceType.craftTimeFromOresAndAlloys * it.quantity }.sum()

    // TODO merge resources to have max one CountedResources per resource type
    operator fun plus(other: Resources) = Resources(resources + other.resources)

    override fun toString(): String = if(resources.isEmpty()) {
        "no resources"
    } else {
        resources.joinToString(", ") { "${it.quantity} ${it.resourceType}" }
    }

    companion object {
        val NOTHING = Resources(emptyList())

        fun of(vararg countedResources: CountedResource): Resources = Resources(countedResources.toList())
    }
}

data class CountedResource(
    val resourceType: ResourceType,
    val quantity: Int
)
