package org.hildan.ipm.helper.galaxy

inline class Price(val amount: Double) {

    constructor(amount: Int) : this(amount.toDouble())

    operator fun times(factor: Double) = Price(amount * factor)

    override fun toString(): String = when {
        amount < 1_000 -> format(amount, "")
        amount < 1_000_000 -> format(amount / 1_000, "k")
        amount < 1_000_000_000 -> format(amount / 1_000_000, "M")
        else -> format(amount / 1_000_000_000, "B")
    }

    private fun format(x: Double, unit: String): String = String.format("\$%.2f$unit", x)
}
