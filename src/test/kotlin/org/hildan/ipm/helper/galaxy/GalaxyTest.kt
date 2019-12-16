package org.hildan.ipm.helper.galaxy

import kotlin.test.Test
import kotlin.test.assertEquals

internal class GalaxyTest {

    @Test
    fun test() {
        val galaxy = Galaxy(ConstantBonuses.NONE).withBoughtPlanet(PlanetType.BALOR)

        assertEquals(Price(2.0), galaxy.totalIncomePerSecond)
    }
}
