package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.Price
import org.hildan.ipm.helper.galaxy.resources.OreType.ALUMINUM
import org.hildan.ipm.helper.galaxy.resources.OreType.COPPER
import org.hildan.ipm.helper.galaxy.resources.OreType.GOLD
import org.hildan.ipm.helper.galaxy.resources.OreType.IRON
import org.hildan.ipm.helper.galaxy.resources.OreType.LEAD
import org.hildan.ipm.helper.galaxy.resources.OreType.SILICON
import org.hildan.ipm.helper.galaxy.resources.OreType.SILVER
import java.time.Duration

enum class AlloyType(
    override val baseValue: Price,
    val recipeUnlockPrice: Price,
    override val smeltTime: Duration,
    override val requiredResources: Resources
) : ResourceType {
    COPPER_BAR(Price(1_450), Price(0), 20.sec(), Resources.of(1000 of COPPER)),
    IRON_BAR(Price(3_000), Price(3_000), 30.sec(), Resources.of(1000 of IRON)),
    LEAD_BAR(Price(6_100), Price(9_000), 40.sec(), Resources.of(1000 of LEAD)),
    SILICON_BAR(Price(12_500), Price(25_000), 1.min(), Resources.of(1000 of SILICON)),
    ALUMINUM_BAR(Price(27_600), Price(75_000), 80.sec(), Resources.of(1000 of ALUMINUM)),
    SILVER_BAR(Price(60_000), Price(225_000), 2.min(), Resources.of(1000 of SILVER)),
    GOLD_BAR(Price(120_000), Price(500_000), 3.min(), Resources.of(1000 of GOLD)),
    BRONZE(Price(234_000), Price(1_000_000), 4.min(), Resources.of(2 of SILVER_BAR, 10 of COPPER_BAR)),
    STEEL(Price(340_000), Price(2_000_000), 8.min(), Resources.of(15 of LEAD_BAR, 30 of IRON_BAR)),
    // TODO fill remaining alloys from https://idle-planet-miner.fandom.com/wiki/Alloy
    ;

    override val craftTime: Duration = Duration.ZERO
}
