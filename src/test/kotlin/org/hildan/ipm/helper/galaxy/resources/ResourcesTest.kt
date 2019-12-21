package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.resources.ItemType.BATTERY
import org.hildan.ipm.helper.galaxy.resources.OreType.COPPER
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourcesTest {

    @Test
    fun `simple ores have no smelt and craft time`() {
        val resources = Resources.of(1 of COPPER)
        assertEquals(Duration.ZERO, resources.totalSmeltTimeFromOre)
        assertEquals(Duration.ZERO, resources.totalCraftTimeFromOresAndAlloys)
    }

    @Test
    fun `items have correct smelt and craft durations`() {
        val resources = Resources.of(1 of BATTERY)
        // 20s x (10 + 2x5)
        assertEquals(400.sec(), resources.totalSmeltTimeFromOre)
        // 4min + 2x 1min
        assertEquals(6.min(), resources.totalCraftTimeFromOresAndAlloys)
    }
}
