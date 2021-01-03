package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.ConstantBonusesSamples
import org.hildan.ipm.helper.galaxy.GalaxyBonuses
import org.hildan.ipm.helper.galaxy.assertDoubleEquals
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.sumRates
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import kotlin.time.seconds

class ProductionKtTest {

    private val NO_BONUSES = GalaxyBonuses(ConstantBonusesSamples.NONE)

    @Test
    fun optimalRecipes_noBonuses_emptyWhenNoSmeltersOrCrafters() {
        val expectedSetup = SmeltersCraftersSetup(emptyList(), emptyList(), ValueRate.ZERO)
        assertEquals(expectedSetup, optimalRecipesFor(setOf(AlloyType.COPPER_BAR), 0, 0, NO_BONUSES))
    }

    @Test
    fun optimalRecipes_noBonuses_singleSmelter() {
        val actualCopperBarIncome = AlloyType.COPPER_BAR.simpleIncome
        val copperOreIncomeConsumption = AlloyType.COPPER_BAR.simpleConsumption
        val expectedSetup = SmeltersCraftersSetup(
            smelters = listOf(AlloyType.COPPER_BAR),
            crafters = emptyList(),
            totalIncome = actualCopperBarIncome - copperOreIncomeConsumption,
        )
        assertEquals(expectedSetup, optimalRecipesFor(setOf(AlloyType.COPPER_BAR), 1, 0, NO_BONUSES))
    }

    @Test
    fun optimalRecipes_noBonuses_preferBetterValue() {
        val actualIronBarIncome = AlloyType.IRON_BAR.simpleIncome
        val ironOreConsumption = AlloyType.IRON_BAR.simpleConsumption
        val expectedSetup = SmeltersCraftersSetup(
            smelters = listOf(AlloyType.IRON_BAR),
            crafters = emptyList(),
            totalIncome = actualIronBarIncome - ironOreConsumption,
        )
        assertEquals(expectedSetup, optimalRecipesFor(setOf(AlloyType.COPPER_BAR, AlloyType.IRON_BAR), 1, 0, NO_BONUSES))
    }

    @Test
    fun optimalRecipes_noBonuses_twoSmelters() {
        val actualIronBarIncome = AlloyType.IRON_BAR.simpleIncome * 2
        val ironOreConsumption = AlloyType.IRON_BAR.simpleConsumption * 2
        val expectedSetup = SmeltersCraftersSetup(
            smelters = listOf(AlloyType.IRON_BAR, AlloyType.IRON_BAR),
            crafters = emptyList(),
            totalIncome = actualIronBarIncome - ironOreConsumption,
        )
        assertEquals(expectedSetup, optimalRecipesFor(setOf(AlloyType.COPPER_BAR, AlloyType.IRON_BAR), 2, 0, NO_BONUSES))
    }

    @Test
    fun optimalRecipes_noBonuses_supportsPartialProduction() {
        val copperWireIncome = ItemType.COPPER_WIRE.simpleIncome
        val actualCopperWireIncome = copperWireIncome * 3.0 / 5.0 // only 3 COPPER_BAR every minute instead of 5
        val actualCopperBarIncome = ValueRate.ZERO // all copper bars used for copper wire, no gain
        val copperOreConsumption = AlloyType.COPPER_BAR.simpleConsumption

        val expectedSetup = SmeltersCraftersSetup(
            smelters = listOf(AlloyType.COPPER_BAR),
            crafters = listOf(ItemType.COPPER_WIRE),
            totalIncome = actualCopperWireIncome + actualCopperBarIncome - copperOreConsumption,
        )
        val unlockedResources = setOf(AlloyType.COPPER_BAR, AlloyType.IRON_BAR, ItemType.COPPER_WIRE)
        assertRecipeSetupEquals(expectedSetup, optimalRecipesFor(unlockedResources, 1, 1, NO_BONUSES))
    }

    @Test
    fun optimalRecipes_noBonuses_useSmelterForRequiredAlloys() {
        val copperWireIncome = ItemType.COPPER_WIRE.simpleIncome
        val copperBarIncome = AlloyType.COPPER_BAR.simpleIncome * 2 // 2 bars in parallel
        val copperOreConsumptionRate = AlloyType.COPPER_BAR.simpleConsumption * 2 // 2 bars in parallel
        val actualCopperBarIncome = copperBarIncome / 6.0 // we get only 1 bar out of the 6 every minute

        val expectedSetup = SmeltersCraftersSetup(
            smelters = listOf(AlloyType.COPPER_BAR, AlloyType.COPPER_BAR),
            crafters = listOf(ItemType.COPPER_WIRE),
            totalIncome = copperWireIncome + actualCopperBarIncome - copperOreConsumptionRate,
        )
        val unlockedResources = setOf(AlloyType.COPPER_BAR, AlloyType.IRON_BAR, ItemType.COPPER_WIRE)
        assertRecipeSetupEquals(expectedSetup, optimalRecipesFor(unlockedResources, 2, 1, NO_BONUSES))
    }

    private fun assertRecipeSetupEquals(expected: SmeltersCraftersSetup, actual: SmeltersCraftersSetup) {
        assertEquals(expected.smelters, actual.smelters)
        assertEquals(expected.crafters, actual.crafters)
        assertDoubleEquals(expected.totalIncome.amountPerSec, actual.totalIncome.amountPerSec,
            "Expected income to be ${expected.totalIncome}, got ${actual.totalIncome}")
    }

    private val AlloyType.simpleIncome: ValueRate
        get() = baseValue / smeltTime

    private val ItemType.simpleIncome: ValueRate
        get() = baseValue / craftTime

    private val AlloyType.simpleConsumption: ValueRate
        get() = requiredResources.quantitiesByType.map { (t, q) -> t.baseValue * q / smeltTime }.sumRates()
}