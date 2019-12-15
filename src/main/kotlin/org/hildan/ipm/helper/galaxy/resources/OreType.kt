package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.Price
import java.time.Duration

enum class OreType(
    override val baseValue: Price
) : ResourceType {
    COPPER(Price(1)),
    IRON(Price(2)),
    LEAD(Price(4)),
    SILICON(Price(8)),
    ALUMINUM(Price(17)),
    SILVER(Price(36)),
    GOLD(Price(75)),
    DIAMOND(Price(160)),
    PLATINUM(Price(340)),
    TITANIUM(Price(730)),
    IRIDIUM(Price(1_600)),
    PALADIUM(Price(3_500)),
    OSMIUM(Price(7_800)),
    RHODIUM(Price(17_500)),
    INETON(Price(40_000)),
    QUADIUM(Price(92_000)),
    SCRITH(Price(215_000)),
    URU(Price(510_000)),
    VIBRANIUM(Price(1_250_000)),
    AETHER(Price(3_200_000));

    override val requiredResources: Resources = Resources.NOTHING
    override val craftTime: Duration = Duration.ZERO
    override val smeltTime: Duration = Duration.ZERO
}
