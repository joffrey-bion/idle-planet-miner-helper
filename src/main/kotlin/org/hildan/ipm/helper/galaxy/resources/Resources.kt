package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import java.time.Duration

infix fun Int.of(resourceType: ResourceType): CountedResource = CountedResource(resourceType, this)

interface ResourceType {
    val baseValue: Price
    val requiredResources: Resources
    val smeltTime: Duration
    val craftTime: Duration

    companion object {
        fun all(): List<ResourceType> = emptyList<ResourceType>() + OreType.values() + AlloyType.values() + ItemType.values()
    }
}

data class Resources(
    val resources: List<CountedResource>
) {
    private val resourceTypes: Set<ResourceType> = resources.mapTo(HashSet()) { it.resourceType }

    val allResourceTypes: Set<ResourceType> = resourceTypes.flatMapTo(HashSet()) { it.requiredResources.allResourceTypes + it }

    val hasAlloys = allResourceTypes.any { it is AlloyType }

    val hasItems = allResourceTypes.any { it is ItemType }

    // TODO merge resources to have max one CountedResources per resource type (unless less efficient)
    operator fun plus(other: Resources) = Resources(resources + other.resources)

    override fun toString(): String = if (resources.isEmpty()) {
        "no resources"
    } else {
        resources.joinToString(", ") { "${it.quantity} ${it.resourceType}" }
    }

    override fun equals(other: Any?): Boolean =
            other is Resources && resources.normalized() == other.resources.normalized()

    override fun hashCode(): Int = resources.normalized().hashCode()

    companion object {
        val NOTHING = Resources(emptyList())

        fun of(vararg countedResources: CountedResource): Resources = Resources(countedResources.toList())
    }
}

data class CountedResource(
    val resourceType: ResourceType,
    val quantity: Int
)

private fun List<CountedResource>.normalized(): List<CountedResource> {
    val quantities = mutableMapOf<ResourceType, Int>()
    forEach { quantities.merge(it.resourceType, it.quantity) { q1, q2 -> q1 + q2 } }
    return quantities.map { CountedResource(it.key, it.value) }.sortedWith(countedResourceComparator)
}

private val countedResourceComparator = Comparator<CountedResource> { cr1, cr2 ->
    resourcesComparator.compare(cr1.resourceType, cr2.resourceType)
}

private val resourcesComparator: Comparator<ResourceType> =
        Comparator { rt1, rt2 ->
            when (rt1) {
                is OreType -> if (rt2 is OreType) rt1.compareTo(rt2) else -1
                is AlloyType -> when (rt2) {
                    is OreType -> 1
                    is AlloyType -> rt1.compareTo(rt2)
                    else -> -1
                }
                is ItemType -> if (rt2 is ItemType) rt1.compareTo(rt2) else 1
                else -> throw IllegalArgumentException("Unknown resource type ${rt1::class}")
            }
        }
