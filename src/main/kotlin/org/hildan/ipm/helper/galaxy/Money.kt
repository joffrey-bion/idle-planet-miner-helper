package org.hildan.ipm.helper.galaxy

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
    private val multipliers: Map<Sellable, Multiplier> = emptyMap(),
    private val stars: Map<Sellable, Int> = emptyMap()
) {
    private val sellPrice = Sellable.all().associateWith { computePrice(it) }

    fun withMultiplier(item: Sellable, factor: Double) = copy(multipliers = multipliers + (item to Multiplier(factor)))

    fun withStars(item: Sellable, nbStars: Int) = copy(stars = stars + (item to nbStars))

    fun getSellPrice(item: Sellable): Price = sellPrice[item] ?: error("No sell price found for item $item")

    private fun computePrice(item: Sellable): Price {
        val nbStars = stars[item] ?: 0
        val multiplier = multipliers[item] ?: Multiplier.NONE
        val basePrice = item.baseSellValue * (1 + 0.2 * nbStars)
        return multiplier.applyTo(basePrice)
    }

    override fun toString(): String {
        return "Market:\n  ${sellPrice.map { "${it.key} = ${it.value}" }.joinToString("\n  ")}"
    }
}
