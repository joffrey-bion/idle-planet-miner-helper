package org.hildan.ipm.helper.galaxy

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

interface Sellable {
    val baseSellValue: Price
}

@UseExperimental(ExperimentalTime::class)
private fun Int.sec() = toDuration(DurationUnit.SECONDS)

enum class OreType(
    override val baseSellValue: Price
) : Sellable {
    COPPER(Price(1)),
    IRON(Price(2)),
    LEAD(Price(4)),
    SILICON(Price(8)),
    ALUMINUM(Price(17)),
    SILVER(Price(36)),
    GOLD(Price(75)),
    DIAMOND(Price(160)),
    PLATINUM(Price(340))
}

@UseExperimental(ExperimentalTime::class)
enum class AlloyType(
    val recipeUnlockPrice: Price,
    override val baseSellValue: Price,
    val baseSmeltTime: Duration
) : Sellable {
    COPPER_BAR(Price(0), Price(1_450), 20.sec()),
    IRON_BAR(Price(3_000), Price(3_000), 30.sec()),
    LEAD_BAR(Price(9_000), Price(6_100), 40.sec()),
    SILICON_BAR(Price(25_000), Price(12_500), 60.sec()),
    ALUMINUM_BAR(Price(75_000), Price(27_600), 80.sec()),
    SILVER_BAR(Price(225_000), Price(60_000), 120.sec()),
    GOLD_BAR(Price(500_000), Price(120_000), 180.sec()),
    BRONZE(Price(1_000_000), Price(234_000), 240.sec())
}

@UseExperimental(ExperimentalTime::class)
enum class Item(
    val recipeUnlockPrice: Price,
    override val baseSellValue: Price,
    val baseCraftTime: Duration
) : Sellable {
    COPPER_WIRE(Price(0), Price(10_000), 60.sec()),
    IRON_NAIL(Price(20_000), Price(20_000), 120.sec()),
    BATTERY(Price(50_000), Price(70_000), 240.sec()),
    HAMMER(Price(100_000), Price(135_000), 480.sec()),
    GLASS(Price(200_000), Price(215_000), 720.sec()),
    CIRCUIT(Price(400_000), Price(620_000), 1200.sec()),
    LENSE(Price(1_000_000), Price(1_100_000), 2400.sec())
}
