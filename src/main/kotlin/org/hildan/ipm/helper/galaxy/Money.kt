package org.hildan.ipm.helper.galaxy

inline class Price(val amount: Double) {

    constructor(amount: Int) : this(amount.toDouble())

    operator fun times(factor: Double) = Price(amount * factor)
}
