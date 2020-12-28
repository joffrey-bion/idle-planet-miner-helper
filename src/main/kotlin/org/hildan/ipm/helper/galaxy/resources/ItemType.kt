package org.hildan.ipm.helper.galaxy.resources

import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.AlloyType.*
import kotlin.time.Duration
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

enum class ItemType(
    override val baseValue: Price,
    val recipeUnlockPrice: Price,
    override val craftTime: Duration,
    override val requiredResources: Resources
) : ResourceType {
    COPPER_WIRE(Price(10_000), Price(0), 1.minutes, Resources.of(5 of COPPER_BAR)),
    IRON_NAIL(Price(20_000), Price(20_000), 2.minutes, Resources.of(5 of IRON_BAR)),
    BATTERY(Price(70_000), Price(50_000), 4.minutes, Resources.of(2 of COPPER_WIRE, 10 of COPPER_BAR)),
    HAMMER(Price(135_000), Price(100_000), 8.minutes, Resources.of(2 of IRON_NAIL, 5 of LEAD_BAR)),
    GLASS(Price(215_000), Price(200_000), 12.minutes, Resources.of(10 of SILICON_BAR)),
    CIRCUIT(Price(620_000), Price(400_000), 20.minutes, Resources.of(5 of ALUMINUM_BAR, 5 of SILICON_BAR, 10 of COPPER_WIRE)),
    LENSE(Price(1_100_000), Price(1_000_000), 40.minutes, Resources.of(1 of GLASS, 5 of SILVER_BAR)),
    LASER(Price(3_200_000), Price(2_000_000), 1.hours, Resources.of(10 of IRON_BAR, 5 of GOLD_BAR, 1 of LENSE)),
    BASIC_COMPUTER(Price(7_600_000), Price(5_000_000), 80.minutes, Resources.of(5 of CIRCUIT, 5 of SILVER_BAR)),
    SOLAR_PANEL(Price(15_000_000), Price(10_000_000), 100.minutes, Resources.of(5 of CIRCUIT, 10 of GLASS)),
    LASER_TORCH(Price(30_000_000), Price(20_000_000), 7200.seconds, Resources.of(5 of BRONZE_BAR, 2 of LASER, 5 of LENSE)),
    ADVANCED_BATTERY(Price(35_000_000), Price(30_000_000), 9000.seconds, Resources.of(20 of STEEL_BAR, 30 of BATTERY)),
    THERMAL_SCANNER(Price(71_500_000), Price(50_000_000), 10750.seconds, Resources.of(5 of PLATINUM_BAR, 2 of LASER, 5 of GLASS)),
    ADVANCED_COMPUTER(Price(180_000_000), Price(120_000_000), 12500.seconds, Resources.of(5 of TITANIUM_BAR, 5 of BASIC_COMPUTER)),
    NAVIGATION_MODULE(Price(960_000_000), Price(250_000_000), 13500.seconds, Resources.of(1 of THERMAL_SCANNER, 2 of LASER_TORCH)),
    PLASMA_TORCH(Price(1_080_000_000), Price(550_000_000), 15000.seconds, Resources.of(15 of IRIDIUM_BAR, 5 of LASER_TORCH)),
    RADIO_TOWER(Price(1_450_000_000), Price(1_500_000_000), 15600.seconds, Resources.of(60 of PLATINUM_BAR, 150 of ALUMINUM_BAR, 40 of TITANIUM_BAR)),
    TELESCOPE(Price(2_500_000_000), Price(5_000_000_000), 16800.seconds, Resources.of(20 of LENSE, 1 of ADVANCED_COMPUTER)),
    SATELLITE_DISH(Price(3_400_000_000), Price(17_500_000_000), 18000.seconds, Resources.of(150 of STEEL_BAR, 20 of PALADIUM_BAR)),
    MOTOR(Price(7_000_000_000), Price(60_000_000_000), 19200.seconds, Resources.of(400 of BRONZE_BAR, 200 of HAMMER)),
    ACCUMULATOR(Price(10_000_000_000), Price(100_000_000_000), 20400.seconds, Resources.of(20 of OSMIUM_BAR, 2 of ADVANCED_BATTERY)),
    NUCLEAR_CAPSULE(Price(23_000_000_000), Price(250_000_000_000), 21000.seconds, Resources.of(5 of RHODIUM_BAR, 1 of PLASMA_TORCH)),
    WIND_TURBINE(Price(140_000_000_000), Price(500_000_000_000), 21600.seconds, Resources.of(150 of ALUMINUM_BAR, 1 of MOTOR)),
//    SPACE_PROBE(Price(180_000_000_000), Price(1_000_000_000_000), XX.sec(), Resources.of(1 of SATELLITE_DISH, 1 of TELESCOPE, 20 of SOLAR_PANEL)),
//    NUCLEAR_REACTOR(Price(1_000_000_000_000), Price(2_000_000_000_000), XX.sec(), Resources.of(1 of NUCLEAR_CAPSULE, 300 of IRIDIUM_BAR)),
    ;

    override val smeltTime: Duration = Duration.ZERO
}
