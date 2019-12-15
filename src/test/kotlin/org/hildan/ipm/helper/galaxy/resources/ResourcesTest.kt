package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.resources.AlloyType.*
import org.hildan.ipm.helper.galaxy.resources.OreType.*
import org.hildan.ipm.helper.galaxy.resources.ItemType.*
import kotlin.test.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourcesTest {

    @Test
    fun `simple ores have no smelt and craft time`() {
        val resources = Resources.of(1 of COPPER)
        assertEquals(Duration.ZERO, resources.totalSmeltTimeFromOre)
        assertEquals(Duration.ZERO, resources.totalCraftTimeFromOresAndAlloys)
    }

    @Test
    fun `simple ores have no highest alloy and item`() {
        val resources = Resources.of(4 of COPPER, 1 of IRON)
        assertEquals(IRON, resources.highestOre)
        assertNull(resources.highestAlloy)
        assertNull(resources.highestItem)
    }

    @Test
    fun `alloys have correct highest ore and alloy`() {
        val resources = Resources.of(3 of LEAD_BAR)
        assertEquals(LEAD, resources.highestOre)
        assertEquals(LEAD_BAR, resources.highestAlloy)
        assertNull(resources.highestItem)
    }

    @Test
    fun `items have correct highest resources`() {
        val resources = Resources.of(1 of BATTERY)
        assertEquals(COPPER, resources.highestOre)
        assertEquals(COPPER_BAR, resources.highestAlloy)
        assertEquals(BATTERY, resources.highestItem)

        val resources2 = Resources.of(1 of LASER)
        assertEquals(GOLD, resources2.highestOre)
        assertEquals(GOLD_BAR, resources2.highestAlloy)
        assertEquals(LASER, resources2.highestItem)

        val resources3 = Resources.of(1 of LENSE)
        assertEquals(SILVER, resources3.highestOre)
        assertEquals(SILVER_BAR, resources3.highestAlloy)
        assertEquals(LENSE, resources3.highestItem)
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
