package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.planets.PlanetProduction
import org.hildan.ipm.helper.galaxy.planets.Planet
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class GalaxyTest {

    @Test
    fun `planet stats are correct`() {
        val constantBonuses = ConstantBonusesSamples.SAMPLE_1

        val galaxy = Galaxy.init(constantBonuses)
            .withBoughtPlanet(Planet.BALOR)
            .withLevels(Planet.BALOR, 50, 30, 30)
            .withColony(Planet.BALOR, 4, PlanetBonus.of(mineRate = 2.0))
            .withProjects(ProjectSamples.BASIC_PROJECTS)
            .withProjects(ProjectSamples.ADVANCED_GATHERING_PROJECTS)

        val balorProduction = galaxy.planets.production[Planet.BALOR]
        assertPlanetStats(PlanetProduction(Rate(1589.86), Rate(116.10), 690.0), balorProduction)
    }
}

private fun assertPlanetStats(expected: PlanetProduction, actual: PlanetProduction?) {
    assertNotNull(actual, "Planet stats should be available")
    assertDoubleEquals(expected.mineRate.timesPerSecond, actual.mineRate.timesPerSecond, "Incorrect mine rate")
    assertDoubleEquals(expected.shipSpeed.timesPerSecond, actual.shipSpeed.timesPerSecond, "Incorrect ship speed")
    assertDoubleEquals(expected.cargo, actual.cargo, "Incorrect cargo size")
}
