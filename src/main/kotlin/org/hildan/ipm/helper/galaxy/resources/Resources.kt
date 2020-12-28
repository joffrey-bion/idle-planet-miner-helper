package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.utils.mergedWith
import kotlin.time.Duration
import java.util.EnumSet
import kotlin.math.roundToInt

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
    val resources: Map<ResourceType, Int>
) {
    private val resourceTypes: Set<ResourceType>
        get() = resources.keys

    val allResourceTypes: Set<ResourceType> by lazy {
        resourceTypes + resourceTypes.flatMapTo(HashSet<ResourceType>()) { it.requiredResources.allResourceTypes }
    }

    val allOreTypes: Set<OreType> by lazy {
        allResourceTypes.filterIsInstanceTo(EnumSet.noneOf(OreType::class.java))
    }

    val allAlloyTypes: Set<AlloyType> by lazy {
        allResourceTypes.filterIsInstanceTo(EnumSet.noneOf(AlloyType::class.java))
    }

    val hasAlloys: Boolean
        get() = allOreTypes.isNotEmpty()

    val hasItems: Boolean
        get() = allResourceTypes.any { it is ItemType }

    operator fun plus(other: Resources) = Resources(resources.mergedWith(other.resources, Int::plus))

    operator fun times(factor: Double): Resources =
            Resources(resources.mapValues { (_, qty) -> (qty * factor).roundToInt() })

    override fun toString(): String = if (resources.isEmpty()) {
        "no resources"
    } else {
        resources.entries.joinToString(", ") { (type, qty) -> "$qty $type" }
    }

    companion object {
        val NOTHING = Resources(emptyMap())

        fun of(vararg countedResources: CountedResource): Resources {
            val map = countedResources.associate { it.resourceType to it.quantity }
            require(map.size == countedResources.size) { "Duplicate entries found in resources list" }
            return Resources(map)
        }
    }
}

data class CountedResource(
    val resourceType: ResourceType,
    val quantity: Int
)
