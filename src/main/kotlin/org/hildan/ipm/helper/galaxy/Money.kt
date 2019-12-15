package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.ResourceType

inline class Price(val amount: Double) {

    constructor(amount: Int) : this(amount.toDouble())

    operator fun plus(other: Price) = Price(amount + other.amount)

    operator fun minus(other: Price) = Price(amount - other.amount)

    operator fun div(other: Price) = amount / other.amount

    operator fun times(factor: Double) = Price(amount * factor)

    override fun toString(): String = when {
        amount < 1_000 -> format(amount, "")
        amount < 1_000_000 -> format(amount / 1_000, "k")
        amount < 1_000_000_000 -> format(amount / 1_000_000, "M")
        else -> format(amount / 1_000_000_000, "B")
    }

    private fun format(x: Double, unit: String): String = String.format("\$%.2f$unit", x)

    companion object {
        val ZERO = Price(0.0)
    }
}

data class Market(
    private val multipliers: Map<ResourceType, Multiplier> = emptyMap(),
    private val stars: Map<ResourceType, Int> = emptyMap()
) {
    private val sellPrice = ResourceType.all().associateWith { computePrice(it) }

    fun withMultiplier(resourceType: ResourceType, factor: Double) =
            copy(multipliers = multipliers + (resourceType to Multiplier(factor)))

    fun withStars(resourceType: ResourceType, nbStars: Int) = copy(stars = stars + (resourceType to nbStars))

    fun getSellPrice(resourceType: ResourceType): Price = sellPrice[resourceType] ?: error("No sell price found for item $resourceType")

    private fun computePrice(item: ResourceType): Price {
        val nbStars = stars[item] ?: 0
        val multiplier = multipliers[item] ?: Multiplier.NONE
        val basePrice = item.baseValue * (1 + 0.2 * nbStars)
        return multiplier.applyTo(basePrice)
    }

    override fun toString(): String {
        return "Market:\n  ${sellPrice.map { "${it.key} = ${it.value}" }.joinToString("\n  ")}"
    }
}
