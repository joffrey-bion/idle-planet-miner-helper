package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.OreType.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

enum class AlloyType(
    override val baseValue: Price,
    val recipeUnlockPrice: Price,
    override val smeltTime: Duration,
    override val requiredResources: Resources
) : ResourceType {
    COPPER_BAR(Price(1_450), Price(0), 20.seconds, Resources.of(1000 of COPPER)),
    IRON_BAR(Price(3_000), Price(3_000), 30.seconds, Resources.of(1000 of IRON)),
    LEAD_BAR(Price(6_100), Price(9_000), 40.seconds, Resources.of(1000 of LEAD)),
    SILICON_BAR(Price(12_500), Price(25_000), 1.minutes, Resources.of(1000 of SILICA)),
    ALUMINUM_BAR(Price(27_600), Price(75_000), 80.seconds, Resources.of(1000 of ALUMINUM)),
    SILVER_BAR(Price(60_000), Price(225_000), 2.minutes, Resources.of(1000 of SILVER)),
    GOLD_BAR(Price(120_000), Price(500_000), 3.minutes, Resources.of(1000 of GOLD)),
    BRONZE_BAR(Price(234_000), Price(1_000_000), 4.minutes, Resources.of(2 of SILVER_BAR, 10 of COPPER_BAR)),
    STEEL_BAR(Price(340_000), Price(2_000_000), 8.minutes, Resources.of(15 of LEAD_BAR, 30 of IRON_BAR)),
    PLATINUM_BAR(Price(780_000), Price(4_000_000), 10.minutes, Resources.of(2 of GOLD_BAR, 1000 of PLATINUM)),
    TITANIUM_BAR(Price(1_630_000), Price(8_000_000), 12.minutes, Resources.of(2 of BRONZE_BAR, 1000 of TITANIUM)),
    IRIDIUM_BAR(Price(3_110_000), Price(15_000_000), 14.minutes, Resources.of(2 of STEEL_BAR, 1000 of IRIDIUM)),
    PALADIUM_BAR(Price(7_000_000), Price(30_000_000), 16.minutes, Resources.of(2 of PLATINUM_BAR, 1000 of PALADIUM)),
    OSMIUM_BAR(Price(14_500_000), Price(60_000_000), 18.minutes, Resources.of(2 of TITANIUM_BAR, 1000 of OSMIUM)),
    RHODIUM_BAR(Price(31_000_000), Price(120_000_000), 20.minutes, Resources.of(2 of IRIDIUM_BAR, 1000 of RHODIUM)),
    INERTON_BAR(Price(68_000_000), Price(250_000_000), 24.minutes, Resources.of(2 of PALADIUM_BAR, 1000 of INERTON)),
    QUADIUM_BAR(Price(152_000_000), Price(500_000_000), 28.minutes, Resources.of(2 of OSMIUM_BAR, 1000 of QUADIUM)),
    SCRITH_BAR(Price(352_000_000), Price(1_000_000_000), 32.minutes, Resources.of(2 of RHODIUM_BAR, 1000 of SCRITH)),
    URU_BAR(Price(832_000_000), Price(2_000_000_000), 36.minutes, Resources.of(2 of INERTON_BAR, 1000 of URU)),
    VIBRANIUM_BAR(Price(2_050_000_000), Price(4_000_000_000), 40.minutes, Resources.of(2 of QUADIUM_BAR, 1000 of VIBRANIUM)),
    AETHER_BAR(Price(5_120_000_000), Price(8_000_000_000), 44.minutes, Resources.of(2 of QUADIUM_BAR, 1000 of AETHER));

    override val craftTime: Duration = Duration.ZERO
}

