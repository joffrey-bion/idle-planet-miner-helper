package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.AlloyType.ALUMINUM_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.COPPER_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.GOLD_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.IRON_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.LEAD_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.SILICON_BAR
import org.hildan.ipm.helper.galaxy.resources.AlloyType.SILVER_BAR
import java.time.Duration

enum class ItemType(
    override val baseValue: Price,
    val recipeUnlockPrice: Price,
    override val craftTime: Duration,
    override val requiredResources: Resources
) : ResourceType {
    COPPER_WIRE(Price(10_000), Price(0), 1.min(), Resources.of(5 of COPPER_BAR)),
    IRON_NAIL(Price(20_000), Price(20_000), 2.min(), Resources.of(5 of IRON_BAR)),
    BATTERY(Price(70_000), Price(50_000), 4.min(), Resources.of(2 of COPPER_WIRE, 10 of COPPER_BAR)),
    HAMMER(Price(135_000), Price(100_000), 8.min(), Resources.of(2 of IRON_NAIL, 5 of LEAD_BAR)),
    GLASS(Price(215_000), Price(200_000), 12.min(), Resources.of(10 of SILICON_BAR)),
    CIRCUIT(Price(620_000), Price(400_000), 20.min(), Resources.of(5 of ALUMINUM_BAR, 5 of SILICON_BAR, 10 of COPPER_WIRE)),
    LENSE(Price(1_100_000), Price(1_000_000), 40.min(), Resources.of(1 of GLASS, 5 of SILVER_BAR)),
    LASER(Price(3_200_000), Price(2_000_000), 1.h(), Resources.of(10 of IRON_BAR, 5 of GOLD_BAR, 1 of LENSE)),
    // TODO fill other items based on excel sheet
    ;

    override val smeltTime: Duration = Duration.ZERO
}
