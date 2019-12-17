package org.hildan.ipm.helper.galaxy.money

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {

    @Test
    fun `price operations`() {
        val price10 = Price(10)
        val price10D = Price(10.0)
        val price20 = Price(20)
        val price30 = Price(30)

        assertEquals(price10, price10)
        assertEquals(price10, price10D)
        assertEquals(price30, price10 + price20)
        assertEquals(price10, price30 - price20)
        assertEquals(price30, price10D + price20)
        assertEquals(price20, price10 * 2)
        assertEquals(price30, price10 * 3.0)
        assertEquals(0.5, price10 / price20)
        assertEquals(2.0, price20 / price10)
    }

    @Test
    fun `sums on lists`() {
        assertEquals(Price(30), listOf(Price(7.5), Price(22.5)).sum())
        assertEquals(ValueRate(30.0), listOf(ValueRate(7.5), ValueRate(22.5)).sumRates())
    }

    @Test
    fun `prices and rates`() {
        val tenDollars = Price(10)
        val twoPerSec = Rate(2.0)
        val tenDollarsPerSec = ValueRate(10.0)
        val twentyDollarsPerSec = ValueRate(20.0)

        assertEquals(twentyDollarsPerSec, tenDollars * twoPerSec)
        assertEquals(tenDollarsPerSec, tenDollars / Duration.ofSeconds(1))
        assertEquals(Duration.ofSeconds(1), tenDollars / tenDollarsPerSec)
        assertEquals(Duration.ofMillis(500), tenDollars / twentyDollarsPerSec)
    }
}
