package org.hildan.ipm.helper.galaxy

import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GalaxyTest {

    @Test
    fun `planet stats are correct`() {
        val constantBonuses = ConstantBonusesSamples.SAMPLE_1

        val galaxy = Galaxy(constantBonuses)
            .withBoughtPlanet(PlanetType.BALOR)
            .withLevels(PlanetType.BALOR, 50, 30, 30)
            .withColony(PlanetType.BALOR, 4, PlanetBonus.of(mineRate = 2.0))
            .withProjects(ProjectSamples.BASIC_PROJECTS)
            .withProjects(ProjectSamples.ADVANCED_GATHERING_PROJECTS)

        val balorStats = galaxy.planetStats[PlanetType.BALOR]
        assertPlanetStats(PlanetStats(1589.86, 116.10, 690.0), balorStats)
    }
}

private fun assertPlanetStats(expected: PlanetStats, actual: PlanetStats?) {
    assertNotNull(actual, "Planet stats should be available")
    assertDoubleEquals(expected.mineRate, actual.mineRate, "Incorrect mine rate")
    assertDoubleEquals(expected.shipSpeed, actual.shipSpeed, "Incorrect ship speed")
    assertDoubleEquals(expected.cargo, actual.cargo, "Incorrect cargo size")
}

private fun assertDoubleEquals(expected: Double, actual: Double, message: String? = null) {
    assertEquals(expected.rounded(), actual.rounded(), message)
}

private fun Double.rounded(): Double = round(this * 100) / 100.0
