package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.ResourceType
import java.time.Duration
import kotlin.math.roundToLong

fun min(p1: Price, p2: Price) = if (p1 < p2) p1 else p2

fun List<Price>.sum() = fold(Price.ZERO) { p1, p2 -> p1 + p2}

fun List<ValueRate>.sumRates() = fold(ValueRate.ZERO) { vr1, vr2 -> vr1 + vr2}

fun Double.perSecond() = Rate(this)

inline class Rate(val timesPerSecond: Double)

inline class ValueRate(private val amountPerSec: Double) : Comparable<ValueRate> {

    val amountPerMillisecond: Price get() = Price(amountPerSec * 1000)

    operator fun plus(other: ValueRate) = ValueRate(amountPerSec + other.amountPerSec)

    operator fun minus(other: ValueRate) = ValueRate(amountPerSec - other.amountPerSec)

    override fun compareTo(other: ValueRate): Int = amountPerSec.compareTo(other.amountPerSec)

    companion object {
        val ZERO = ValueRate(0.0)
    }
}

inline class Price(private val amount: Double) : Comparable<Price> {

    constructor(amount: Int) : this(amount.toDouble())

    operator fun plus(other: Price) = Price(amount + other.amount)

    operator fun minus(other: Price) = Price(amount - other.amount)

    operator fun div(other: Price) = amount / other.amount

    operator fun div(time: Duration): ValueRate = ValueRate(amount / time.toSeconds())

    operator fun div(rate: ValueRate): Duration = if (rate == ValueRate.ZERO) {
        Duration.ofDays(1000) // Infinite time
    } else {
        Duration.ofMillis((this / rate.amountPerMillisecond).roundToLong())
    }

    operator fun times(factor: Double) = Price(amount * factor)

    operator fun times(factor: Int) = Price(amount * factor)

    operator fun times(rate: Rate): ValueRate = ValueRate(amount * rate.timesPerSecond)

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

    override fun compareTo(other: Price): Int = amount.compareTo(other.amount)
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
