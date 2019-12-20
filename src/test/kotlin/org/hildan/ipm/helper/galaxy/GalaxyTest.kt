package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.planets.PlanetStats
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import kotlin.test.Test
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
        assertPlanetStats(PlanetStats(Rate(1589.86), Rate(116.10), 690.0), balorStats)
    }
}

private fun assertPlanetStats(expected: PlanetStats, actual: PlanetStats?) {
    assertNotNull(actual, "Planet stats should be available")
    assertDoubleEquals(expected.mineRate.timesPerSecond, actual.mineRate.timesPerSecond, "Incorrect mine rate")
    assertDoubleEquals(expected.shipSpeed.timesPerSecond, actual.shipSpeed.timesPerSecond, "Incorrect ship speed")
    assertDoubleEquals(expected.cargo, actual.cargo, "Incorrect cargo size")
}
