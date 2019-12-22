package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.ConstantBonusesSamples
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.ItemType.BATTERY
import org.hildan.ipm.helper.galaxy.resources.OreType.COPPER
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import org.hildan.ipm.helper.utils.min
import org.hildan.ipm.helper.utils.sec
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class BonusesTest {

    @Test
    fun `simple ores have no smelt and craft time`() {
        val bonuses = Bonuses(ConstantBonusesSamples.NONE)
        val resources = Resources.of(1 of COPPER)
        assertEquals(Duration.ZERO, with(bonuses) { resources.totalSmeltTimeFromOre })
        assertEquals(Duration.ZERO, with(bonuses) { resources.totalCraftTimeFromOresAndAlloys })
    }

    @Test
    fun `items have correct smelt and craft durations`() {
        val bonuses = Bonuses(ConstantBonusesSamples.NONE)
        val resources = Resources.of(1 of BATTERY)
        // 20s x (10 + 2x5)
        assertEquals(400.sec(), with(bonuses) { resources.totalSmeltTimeFromOre })
        // 4min + 2x 1min
        assertEquals(6.min(), with(bonuses) {resources.totalCraftTimeFromOresAndAlloys })
    }
}
