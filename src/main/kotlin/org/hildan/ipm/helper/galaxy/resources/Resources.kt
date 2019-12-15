package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.Price
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

    val highestOre: OreType? =
            (resourceTypes.mapNotNull { it.requiredResources.highestOre } + resourceTypes.filterIsInstance<OreType>()).max()

    val highestAlloy: AlloyType? =
            (resourceTypes.mapNotNull { it.requiredResources.highestAlloy } + resourceTypes.filterIsInstance<AlloyType>()).max()

    val highestItem: ItemType? =
            (resourceTypes.mapNotNull { it.requiredResources.highestItem } + resourceTypes.filterIsInstance<ItemType>()).max()

    val totalSmeltTimeFromOre: Duration =
            resources.map { it.resourceType.smeltTimeFromOre * it.quantity }.sum()

    val totalCraftTimeFromOresAndAlloys: Duration =
            resources.map { it.resourceType.craftTimeFromOresAndAlloys * it.quantity }.sum()

    companion object {
        val NOTHING = Resources(emptyList())

        fun of(vararg countedResources: CountedResource): Resources = Resources(countedResources.toList())
    }
}

data class CountedResource(
    val resourceType: ResourceType,
    val quantity: Int
)
