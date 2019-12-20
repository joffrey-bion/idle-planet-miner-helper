package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.resources.OreType.COPPER
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.of
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplierTest {

    private val copper10 = Resources.of(10 of COPPER)
    private val copper9 = Resources.of(9 of COPPER)
    private val copper8 = Resources.of(8 of COPPER)
    private val copper7 = Resources.of(7 of COPPER)
    private val copper5 = Resources.of(5 of COPPER)
    private val copper4 = Resources.of(4 of COPPER)

    @Test
    fun `multiply double`() {
        assertEquals(0.8, Multiplier(0.8).applyTo(1.0))
        assertEquals(0.5, Multiplier(0.5).applyTo(1.0))
        assertEquals(5.0, Multiplier(0.5).applyTo(10.0))
    }

    @Test
    fun `multiplier 1 doesn't affect anything`() {
        (0..10).forEach {
            assertEquals(it.toDouble(), Multiplier(1.0).applyTo(it.toDouble()))
            assertSingleReduction(it, it, 1.0)
        }
        assertEquals(2.5, Multiplier(1.0).applyTo(2.5))
    }

    @Test
    fun `apply to resources`() {
        assertSingleReduction(8, 10, 0.8)
        assertSingleReduction(9, 10, 0.9)
        assertSingleReduction(7, 10, 0.7)
        assertSingleReduction(14, 20, 0.7)
        assertSingleReduction(35, 50, 0.7)
        assertSingleReduction(140, 200, 0.7)
    }

    @Test
    fun `apply to resources with correct rounding`() {
        assertSingleReduction(2, 3, 0.7)
        assertSingleReduction(4, 5, 0.7)
        assertSingleReduction(18, 25, 0.7)
    }

    private fun assertSingleReduction(expected: Int, initial: Int, multiplier: Double) {
        val initialResources = Resources.of(initial of COPPER)
        val expectedResources = Resources.of(expected of COPPER)
        assertEquals(expectedResources, Multiplier(multiplier).applyTo(initialResources))
    }
}
