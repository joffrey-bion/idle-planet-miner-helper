package org.hildan.ipm.helper.optimizer

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.hildan.ipm.helper.galaxy.*
import org.hildan.ipm.helper.galaxy.money.*
import org.hildan.ipm.helper.galaxy.planets.*
import org.hildan.ipm.helper.galaxy.resources.*
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

internal class AggregatorKtTest {

    private val upgradeBalorMine3 = AppliedAction(
        action = Action.Upgrade.Mine(Planet.BALOR, 3),
        newGalaxy = Galaxy.init(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(10),
        requiredResources = Resources.of(2 of OreType.COPPER, 2 of OreType.IRON),
        time = 1.seconds,
        incomeRateGain = ValueRate(1.0)
    )

    private val upgradeBalorMine4 = AppliedAction(
        action = Action.Upgrade.Mine(Planet.BALOR, 4),
        newGalaxy = Galaxy.init(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(20),
        requiredResources = Resources.of(1 of OreType.COPPER, 4 of OreType.LEAD),
        time = 2.seconds,
        incomeRateGain = ValueRate(2.0)
    )

    private val upgradeBalorMine3Then4 = AppliedAction(
        action = Action.Upgrade.Mine(Planet.BALOR, 4),
        newGalaxy = Galaxy.init(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(30),
        requiredResources = Resources.of(3 of OreType.COPPER, 2 of OreType.IRON, 4 of OreType.LEAD),
        time = 3.seconds,
        incomeRateGain = ValueRate(3.0)
    )

    private val upgradeBalorShip4 = AppliedAction(
        action = Action.Upgrade.Ship(Planet.BALOR, 4),
        newGalaxy = Galaxy.init(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(21),
        requiredResources = Resources.of(21 of OreType.IRON),
        time = 21.seconds,
        incomeRateGain = ValueRate(2.1)
    )

    private val upgradeAnadiusMine4 = AppliedAction(
        action = Action.Upgrade.Mine(Planet.ANADIUS, 4),
        newGalaxy = Galaxy.init(ConstantBonusesSamples.SAMPLE_1),
        requiredCash = Price(20),
        requiredResources = Resources.of(1 of OreType.COPPER, 4 of OreType.LEAD),
        time = 2.seconds,
        incomeRateGain = ValueRate(2.0)
    )

    @Test
    fun `should combine mine upgrades to the same planet`() = runTest {
        val actions = flowOf(upgradeBalorMine3, upgradeBalorMine4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3Then4), compactedActions)
    }

    @Test
    fun `should not combine different types of upgrades`() = runTest {
        val actions = flowOf(upgradeBalorMine3, upgradeBalorShip4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3, upgradeBalorShip4), compactedActions)
    }

    @Test
    fun `should not combine mine upgrades to the different planets`() = runTest {
        val actions = flowOf(upgradeBalorMine3, upgradeAnadiusMine4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorMine3, upgradeAnadiusMine4), compactedActions)
    }

    @Test
    fun `mix of combinable and non combinable`() = runTest {
        val actions = flowOf(upgradeBalorShip4, upgradeBalorMine3, upgradeBalorMine4, upgradeAnadiusMine4)
        val compactedActions = actions.compact().toList()
        assertEquals(listOf(upgradeBalorShip4, upgradeBalorMine3Then4, upgradeAnadiusMine4), compactedActions)
    }
}
