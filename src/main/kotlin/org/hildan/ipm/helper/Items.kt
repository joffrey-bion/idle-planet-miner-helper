package org.hildan.ipm.helper

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

interface Sellable {
    val baseSellValue: Int
}

@UseExperimental(ExperimentalTime::class)
private fun Int.sec() = toDuration(DurationUnit.SECONDS)

enum class OreType(
    override val baseSellValue: Int
) : Sellable {
    COPPER(1),
    IRON(1),
    LEAD(4),
    SILICON(8),
    ALUMINUM(17),
    SILVER(36),
    GOLD(75),
    DIAMOND(160),
    PLATINUM(340)
}

@UseExperimental(ExperimentalTime::class)
enum class AlloyType(
    val recipeUnlockPrice: Int,
    override val baseSellValue: Int,
    val baseSmeltTime: Duration
) : Sellable {
    COPPER_BAR(0, 1_450, 20.sec()),
    IRON_BAR(3_000, 3_000, 30.sec()),
    LEAD_BAR(9_000, 6_100, 40.sec()),
    SILICON_BAR(25_000, 12_500, 60.sec()),
    ALUMINUM_BAR(75_000, 27_600, 80.sec()),
    SILVER_BAR(225_000, 60_000, 120.sec()),
    GOLD_BAR(500_000, 120_000, 180.sec()),
    BRONZE(1_000_000, 234_000, 240.sec())
}

@UseExperimental(ExperimentalTime::class)
enum class Item(
    val recipeUnlockPrice: Int,
    override val baseSellValue: Int,
    val baseCraftTime: Duration
) : Sellable {
    COPPER_WIRE(0, 10_000, 60.sec()),
    IRON_NAIL(20_000, 20_000, 120.sec()),
    BATTERY(50_000, 70_000, 240.sec()),
    HAMMER(100_000, 135_000, 480.sec()),
    GLASS(200_000, 215_000, 720.sec()),
    CIRCUIT(400_000, 620_000, 1200.sec()),
    LENSE(1_000_000, 1_100_000, 2400.sec())
}
