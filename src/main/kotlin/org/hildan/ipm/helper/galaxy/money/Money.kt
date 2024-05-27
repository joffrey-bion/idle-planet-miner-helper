package org.hildan.ipm.helper.galaxy.money

import org.hildan.ipm.helper.utils.formatWithSuffix
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun min(p1: Price, p2: Price) = if (p1 < p2) p1 else p2

fun List<Price>.sum() = fold(Price.ZERO) { p1, p2 -> p1 + p2}

fun List<ValueRate>.sumRates() = fold(ValueRate.ZERO) { vr1, vr2 -> vr1 + vr2}

operator fun Int.div(rate: Rate): Duration = (this / rate.timesPerSecond).seconds

@JvmInline
value class Rate(val timesPerSecond: Double): Comparable<Rate> {

    operator fun plus(other: Rate): Rate = Rate(timesPerSecond + other.timesPerSecond)

    operator fun minus(other: Rate): Rate = Rate(timesPerSecond - other.timesPerSecond)

    operator fun times(factor: Double): Rate = Rate(timesPerSecond * factor)

    operator fun div(factor: Double): Rate = Rate(timesPerSecond / factor)

    override fun compareTo(other: Rate): Int = timesPerSecond.compareTo(other.timesPerSecond)

    override fun toString(): String = String.format("%.2f/s", timesPerSecond)
}

@JvmInline
value class ValueRate(val amountPerSec: Double) : Comparable<ValueRate> {

    operator fun plus(other: ValueRate) = ValueRate(amountPerSec + other.amountPerSec)

    operator fun minus(other: ValueRate) = ValueRate(amountPerSec - other.amountPerSec)

    override fun compareTo(other: ValueRate): Int = amountPerSec.compareTo(other.amountPerSec)

    fun formatPerMinute(): String = String.format("\$%s/min", (amountPerSec * 60).formatWithSuffix())

    override fun toString(): String = String.format("\$%.2f/s", amountPerSec)

    companion object {
        val ZERO = ValueRate(0.0)
    }
}

@JvmInline
value class Price(private val amount: Double) : Comparable<Price> {

    constructor(amount: Int) : this(amount.toDouble())

    constructor(amount: Long) : this(amount.toDouble())

    operator fun plus(other: Price): Price = Price(amount + other.amount)

    operator fun minus(other: Price): Price = Price(amount - other.amount)

    operator fun div(other: Price): Double = amount / other.amount

    operator fun div(time: Duration): ValueRate = ValueRate(amount / time.inWholeSeconds)

    operator fun div(rate: ValueRate): Duration = (amount / rate.amountPerSec).seconds

    operator fun times(factor: Double): Price = Price(amount * factor)

    operator fun times(factor: Int): Price = Price(amount * factor)

    operator fun times(rate: Rate): ValueRate = ValueRate(amount * rate.timesPerSecond)

    override fun compareTo(other: Price): Int = amount.compareTo(other.amount)

    override fun toString(): String = "\$${amount.formatWithSuffix()}"

    companion object {
        val ZERO = Price(0.0)
    }
}

