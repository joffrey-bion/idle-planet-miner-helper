package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.utils.B
import org.hildan.ipm.helper.utils.M
import org.hildan.ipm.helper.utils.T
import org.hildan.ipm.helper.utils.k
import org.hildan.ipm.helper.utils.q

object Smelters {

    const val MAX = 9

    private val smelterPrices = mapOf(
        // first one can't be bought
        2 to Price(50.k()),
        3 to Price(500.k()),
        4 to Price(10.M()),
        5 to Price(5.B()),
        6 to Price(100.B()),
        7 to Price(50.T()),
        8 to Price(500.T()),
        9 to Price(250.q())
    )

    fun priceForOneMore(currentNumber: Int): Price = smelterPrices.getValue(currentNumber + 1)
}

object Crafters {

    const val MAX = 8

    private val crafterPrices = mapOf(
        // first one can't be bought
        2 to Price(1.M()),
        3 to Price(100.M()),
        4 to Price(50.B()),
        5 to Price(1.T()),
        6 to Price(500.T()),
        7 to Price(5.q()),
        8 to Price(2500.q())
    )

    fun priceForOneMore(currentNumber: Int): Price = crafterPrices.getValue(currentNumber + 1)
}
