package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.resources.ItemType.BATTERY
import org.hildan.ipm.helper.galaxy.resources.OreType.COPPER
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import kotlin.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.minutes
import kotlin.time.seconds

class GalaxyBonusesTest {

    @Test
    fun `simple ores have no smelt and craft time`() {
        val bonuses = GalaxyBonuses(ConstantBonusesSamples.NONE)
        val resources = Resources.of(1 of COPPER)
        assertEquals(Duration.ZERO, with(bonuses) { resources.totalSmeltTimeFromOre })
        assertEquals(Duration.ZERO, with(bonuses) { resources.totalCraftTimeFromOresAndAlloys })
    }

    @Test
    fun `items have correct smelt and craft durations`() {
        val bonuses = GalaxyBonuses(ConstantBonusesSamples.NONE)
        val resources = Resources.of(1 of BATTERY)
        // 20s x (10 + 2x5)
        assertEquals(400.seconds, with(bonuses) { resources.totalSmeltTimeFromOre })
        // 4min + 2x 1min
        assertEquals(6.minutes, with(bonuses) { resources.totalCraftTimeFromOresAndAlloys })
    }
}
