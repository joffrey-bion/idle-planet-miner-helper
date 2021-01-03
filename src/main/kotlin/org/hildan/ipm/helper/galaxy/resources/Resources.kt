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
        @OptIn(ExperimentalStdlibApi::class)
        val ALL: List<ResourceType> = buildList {
            addAll(OreType.values())
            addAll(AlloyType.values())
            addAll(ItemType.values())
        }
    }
}

data class Resources(
    val quantitiesByType: Map<ResourceType, Int>
) {
    private val resourceTypes: Set<ResourceType>
        get() = quantitiesByType.keys

    val allResourceTypes: Set<ResourceType> by lazy {
        resourceTypes + resourceTypes.flatMapTo(HashSet()) { it.requiredResources.allResourceTypes }
    }

    val allOreTypes: Set<OreType> by lazy {
        allResourceTypes.filterIsInstanceTo(EnumSet.noneOf(OreType::class.java))
    }

    val allAlloyTypes: Set<AlloyType> by lazy {
        allResourceTypes.filterIsInstanceTo(EnumSet.noneOf(AlloyType::class.java))
    }

    val hasAlloys: Boolean
        get() = allAlloyTypes.isNotEmpty()

    val hasItems: Boolean
        get() = allResourceTypes.any { it is ItemType }

    operator fun plus(other: Resources) = Resources(quantitiesByType.mergedWith(other.quantitiesByType, Int::plus))

    operator fun times(factor: Double): Resources =
            Resources(quantitiesByType.mapValues { (_, qty) -> (qty * factor).roundToInt() })

    override fun toString(): String = if (quantitiesByType.isEmpty()) {
        "no resources"
    } else {
        quantitiesByType.entries.joinToString(", ") { (type, qty) -> "$qty $type" }
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
