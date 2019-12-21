package org.hildan.ipm.helper.optimizer

import org.hildan.ipm.helper.galaxy.ConstantBonusesSamples
import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AggregatorKtTest {

    private val upgradeBalorMine3 = AppliedAction(
        action = Action.Upgrade.Mine(PlanetType.BALOR, 3),
        newGalaxy = Galaxy(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(10),
        requiredResources = Resources.of(2 of OreType.COPPER, 2 of OreType.IRON),
        time = Duration.ofSeconds(1),
        incomeRateGain = ValueRate(1.0)
    )

    private val upgradeBalorMine4 = AppliedAction(
        action = Action.Upgrade.Mine(PlanetType.BALOR, 4),
        newGalaxy = Galaxy(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(20),
        requiredResources = Resources.of(1 of OreType.COPPER, 4 of OreType.LEAD),
        time = Duration.ofSeconds(2),
        incomeRateGain = ValueRate(2.0)
    )

    private val upgradeBalorMine3Then4 = AppliedAction(
        action = Action.Upgrade.Mine(PlanetType.BALOR, 4),
        newGalaxy = Galaxy(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(30),
        requiredResources = Resources.of(3 of OreType.COPPER, 2 of OreType.IRON, 4 of OreType.LEAD),
        time = Duration.ofSeconds(3),
        incomeRateGain = ValueRate(3.0)
    )

    private val upgradeBalorShip4 = AppliedAction(
        action = Action.Upgrade.Ship(PlanetType.BALOR, 4),
        newGalaxy = Galaxy(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(21),
        requiredResources = Resources.of(21 of OreType.IRON),
        time = Duration.ofSeconds(21),
        incomeRateGain = ValueRate(2.1)
    )

    private val upgradeAnadiusMine4 = AppliedAction(
        action = Action.Upgrade.Mine(PlanetType.ANADIUS, 4),
        newGalaxy = Galaxy(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(20),
        requiredResources = Resources.of(1 of OreType.COPPER, 4 of OreType.LEAD),
        time = Duration.ofSeconds(2),
        incomeRateGain = ValueRate(2.0)
    )

    @Test
    fun `should combine mine upgrades to the same planet`() {
        val actions = sequenceOf(upgradeBalorMine3, upgradeBalorMine4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3Then4), compactedActions)
    }

    @Test
    fun `should not combine different types of upgrades`() {
        val actions = sequenceOf(upgradeBalorMine3, upgradeBalorShip4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3, upgradeBalorShip4), compactedActions)
    }

    @Test
    fun `should not combine mine upgrades to the different planets`() {
        val actions = sequenceOf(upgradeBalorMine3, upgradeAnadiusMine4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3, upgradeAnadiusMine4), compactedActions)
    }
}
