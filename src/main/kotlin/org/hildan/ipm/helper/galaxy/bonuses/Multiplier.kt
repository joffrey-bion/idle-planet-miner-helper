package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.resources.CountedResource
import org.hildan.ipm.helper.galaxy.resources.Resources
import java.time.Duration
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

inline class Multiplier(private val factor: Double) {

    operator fun plus(other: Multiplier) = Multiplier(factor + (other.factor - 1))

    operator fun times(other: Multiplier) = Multiplier(factor * other.factor)

    fun pow(n: Int): Multiplier = Multiplier(factor.pow(n))

    fun repeat(n: Int): Multiplier = Multiplier(1 + (factor - 1) * n)

    fun applyTo(value: Double): Double = value * factor

    fun applyTo(price: Price): Price = price * factor

    fun applyTo(rate: Rate): Rate = rate * factor

    fun applyAsSpeed(duration: Duration): Duration = Duration.ofMillis((duration.toMillis() / factor).roundToLong())

    fun applyTo(resources: Resources): Resources = resources * factor

    override fun toString(): String = String.format("x%.2f", factor)

    companion object {
        val NONE = Multiplier(1.0)
    }
}
