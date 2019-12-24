package org.hildan.ipm.helper.galaxy.planets

import org.hildan.ipm.helper.galaxy.TelescopeLevel
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.resources.OreType.*
import kotlin.math.pow

data class OrePart(
    val oreType: OreType,
    val ratio: Double
)

private infix fun Double.of(oreType: OreType) = OrePart(oreType, this)

enum class PlanetType(
    val unlockPrice: Price,
    val telescopeLevel: TelescopeLevel,
    val baseMineRate: Double,
    val distance: Int,
    val oreDistribution: List<OrePart>
) {
    BALOR(
        unlockPrice = Price(100),
        telescopeLevel = TelescopeLevel(0),
        baseMineRate = 0.25,
        distance = 10,
        oreDistribution = listOf(1.0 of COPPER)
    ),
    DRASTA(
        unlockPrice = Price(200),
        telescopeLevel = TelescopeLevel(0),
        baseMineRate = 0.37,
        distance = 12,
        oreDistribution = listOf(0.8 of COPPER, 0.2 of IRON)
    ),
    ANADIUS(
        unlockPrice = Price(500),
        telescopeLevel = TelescopeLevel(0),
        baseMineRate = 0.52,
        distance = 14,
        oreDistribution = listOf(0.5 of COPPER, 0.5 of IRON)
    ),
    DHOLEN(
        unlockPrice = Price(1250),
        telescopeLevel = TelescopeLevel(0),
        baseMineRate = 0.70,
        distance = 15,
        oreDistribution = listOf(0.8 of IRON, 0.2 of LEAD)
    ),
    VERR(
        unlockPrice = Price(5000),
        telescopeLevel = TelescopeLevel(1),
        baseMineRate = 0.92,
        distance = 16,
        oreDistribution = listOf(0.2 of COPPER, 0.3 of IRON, 0.5 of LEAD)
    ),
    NEWTON(
        unlockPrice = Price(9000),
        telescopeLevel = TelescopeLevel(1),
        baseMineRate = 1.18,
        distance = 18,
        oreDistribution = listOf(1.0 of LEAD)
    ),
    WIDOW(
        unlockPrice = Price(15_000),
        telescopeLevel = TelescopeLevel(1),
        baseMineRate = 1.46,
        distance = 20,
        oreDistribution = listOf(0.4 of IRON, 0.4 of COPPER, 0.2 of SILICON)
    ),
    ACHERON(
        unlockPrice = Price(25_000),
        telescopeLevel = TelescopeLevel(2),
        baseMineRate = 1.78,
        distance = 22,
        oreDistribution = listOf(0.6 of SILICON, 0.4 of COPPER)
    ),
    YANGZTE(
        unlockPrice = Price(40_000),
        telescopeLevel = TelescopeLevel(2),
        baseMineRate = 2.14,
        distance = 23,
        oreDistribution = listOf(0.8 of SILICON, 0.2 of ALUMINUM)
    ),
    SOLVEIG(
        unlockPrice = Price(75_000),
        telescopeLevel = TelescopeLevel(2),
        baseMineRate = 2.53,
        distance = 25,
        oreDistribution = listOf(0.5 of ALUMINUM, 0.3 of SILICON, 0.2 of LEAD)
    ),
    IMIR(
        unlockPrice = Price(150_000),
        telescopeLevel = TelescopeLevel(3),
        baseMineRate = 2.95,
        distance = 26,
        oreDistribution = listOf(1.0 of ALUMINUM)
    ),
    RELIC(
        unlockPrice = Price(250_000),
        telescopeLevel = TelescopeLevel(3),
        baseMineRate = 3.41,
        distance = 28,
        oreDistribution = listOf(0.45 of LEAD, 0.35 of SILICON, 0.2 of SILVER)
    ),
    NITH(
        unlockPrice = Price(400_000),
        telescopeLevel = TelescopeLevel(3),
        baseMineRate = 3.90,
        distance = 30,
        oreDistribution = listOf(0.8 of SILVER, 0.2 of ALUMINUM)
    ),
    BATALLA(
        unlockPrice = Price(800_000),
        telescopeLevel = TelescopeLevel(4),
        baseMineRate = 4.42,
        distance = 33,
        oreDistribution = listOf(0.40 of COPPER, 0.40 of IRON, 0.20 of GOLD)
    ),
    MICAH(
        unlockPrice = Price(1_500_000),
        telescopeLevel = TelescopeLevel(4),
        baseMineRate = 4.98,
        distance = 35,
        oreDistribution = listOf(0.50 of GOLD, 0.50 of SILVER)
    ),
    PRANAS(
        unlockPrice = Price(3_000_000),
        telescopeLevel = TelescopeLevel(4),
        baseMineRate = 5.58,
        distance = 37,
        oreDistribution = listOf(1.0 of GOLD)
    ),
    CASTELLUS(
        unlockPrice = Price(6_400_000),
        telescopeLevel = TelescopeLevel(5),
        baseMineRate = 6.20,
        distance = 40,
        oreDistribution = listOf(0.40 of ALUMINUM, 0.35 of SILICON, 0.25 of DIAMOND)
    ),
    GORGON(
        unlockPrice = Price(12_000_000),
        telescopeLevel = TelescopeLevel(5),
        baseMineRate = 6.86,
        distance = 43,
        oreDistribution = listOf(0.80 of DIAMOND, 0.20 of LEAD)
    ),
    PARNITHA(
        unlockPrice = Price(25_000_000),
        telescopeLevel = TelescopeLevel(5),
        baseMineRate = 7.56,
        distance = 45,
        oreDistribution = listOf(0.70 of GOLD, 0.30 of PLATINUM)
    ),
    ORISONI(
        unlockPrice = Price(50_000_000),
        telescopeLevel = TelescopeLevel(6),
        baseMineRate = 8.29,
        distance = 48,
        oreDistribution = listOf(0.70 of PLATINUM, 0.30 of DIAMOND)
    ),
    THESEUS(
        unlockPrice = Price(100_000_000),
        telescopeLevel = TelescopeLevel(6),
        baseMineRate = 9.05,
        distance = 51,
        oreDistribution = listOf(1.0 of PLATINUM)
    ),
    ZELENE(
        unlockPrice = Price(200_000_000),
        telescopeLevel = TelescopeLevel(6),
        baseMineRate = 9.85,
        distance = 54,
        oreDistribution = listOf(0.70 of SILVER, 0.30 of TITANIUM)
    ),
    HAN(
        unlockPrice = Price(400_000_000),
        telescopeLevel = TelescopeLevel(7),
        baseMineRate = 10.68,
        distance = 57,
        oreDistribution = listOf(0.70 of TITANIUM, 0.20 of DIAMOND, 0.10 of GOLD)
    ),
    STRENNUS(
        unlockPrice = Price(800_000_000),
        telescopeLevel = TelescopeLevel(7),
        baseMineRate = 11.54,
        distance = 58,
        oreDistribution = listOf(0.70 of TITANIUM, 0.30 of PLATINUM)
    ),
    OSUN(
        unlockPrice = Price(1_600_000_000),
        telescopeLevel = TelescopeLevel(7),
        baseMineRate = 12.44,
        distance = 60,
        oreDistribution = listOf(0.60 of ALUMINUM, 0.40 of IRIDIUM)
    ),
    PLOITARI(
        unlockPrice = Price(3_200_000_000),
        telescopeLevel = TelescopeLevel(8),
        baseMineRate = 13.38,
        distance = 63,
        oreDistribution = listOf(0.50 of IRIDIUM, 0.50 of DIAMOND)
    ),
    ELYSTA(
        unlockPrice = Price(6_400_000_000),
        telescopeLevel = TelescopeLevel(8),
        baseMineRate = 14.34,
        distance = 67,
        oreDistribution = listOf(1.0 of IRIDIUM)
    ),
    TIKKUN(
        unlockPrice = Price(12_500_000_000),
        telescopeLevel = TelescopeLevel(8),
        baseMineRate = 15.34,
        distance = 70,
        oreDistribution = listOf(0.40 of IRIDIUM, 0.35 of TITANIUM, 0.25 of PALADIUM)
    ),
    SATENT(
        unlockPrice = Price(25_000_000_000),
        telescopeLevel = TelescopeLevel(9),
        baseMineRate = 16.38,
        distance = 72,
        oreDistribution = listOf(0.60 of PALADIUM, 0.40 of TITANIUM)
    ),
    URLARAST(
        unlockPrice = Price(50_000_000_000),
        telescopeLevel = TelescopeLevel(9),
        baseMineRate = 17.45,
        distance = 73,
        oreDistribution = listOf(0.90 of PALADIUM, 0.10 of DIAMOND)
    ),
    VULAR(
        unlockPrice = Price(100_000_000_000),
        telescopeLevel = TelescopeLevel(9),
        baseMineRate = 18.55,
        distance = 75,
        oreDistribution = listOf(0.70 of PALADIUM, 0.30 of OSMIUM)
    ),
    NIBIRU(
        unlockPrice = Price(250_000_000_000),
        telescopeLevel = TelescopeLevel(10),
        baseMineRate = 19.69,
        distance = 76,
        oreDistribution = listOf(0.60 of OSMIUM, 0.40 of IRIDIUM)
    ),
    XENA(
        unlockPrice = Price(600_000_000_000),
        telescopeLevel = TelescopeLevel(10),
        baseMineRate = 20.86,
        distance = 78,
        oreDistribution = listOf(1.0 of OSMIUM)
    ),
    RUPERT(
        unlockPrice = Price(1_500_000_000_000),
        telescopeLevel = TelescopeLevel(10),
        baseMineRate = 22.06,
        distance = 78,
        oreDistribution = listOf(0.55 of PALADIUM, 0.30 of OSMIUM, 0.15 of RHODIUM)
    ),
    PAX(
        unlockPrice = Price(4_000_000_000_000),
        telescopeLevel = TelescopeLevel(11),
        baseMineRate = 23.30,
        distance = 80,
        oreDistribution = listOf(0.50 of RHODIUM, 0.50 of PLATINUM)
    ),
    IVYRA(
        unlockPrice = Price(10_000_000_000_000),
        telescopeLevel = TelescopeLevel(11),
        baseMineRate = 24.58,
        distance = 81,
        oreDistribution = listOf(1.0 of RHODIUM)
    ),
    UTRITS(
        unlockPrice = Price(25_000_000_000_000),
        telescopeLevel = TelescopeLevel(11),
        baseMineRate = 25.88,
        distance = 82,
        oreDistribution = listOf(0.75 of RHODIUM, 0.25 of INERTON)
    ),
    DOOSIE(
        unlockPrice = Price(62_000_000_000_000),
        telescopeLevel = TelescopeLevel(12),
        baseMineRate = 27.22,
        distance = 84,
        oreDistribution = listOf(0.50 of INERTON, 0.50 of OSMIUM)
    ),
    ZULU(
        unlockPrice = Price(160_000_000_000_000),
        telescopeLevel = TelescopeLevel(12),
        baseMineRate = 28.60,
        distance = 84,
        oreDistribution = listOf(1.0 of INERTON)
    ),
    UNICAE(
        unlockPrice = Price(400_000_000_000_000),
        telescopeLevel = TelescopeLevel(12),
        baseMineRate = 30.01,
        distance = 85,
        oreDistribution = listOf(0.80 of INERTON, 0.20 of QUADIUM)
    );
//    DUNE(
//        unlockPrice = Price(1_000_000_000_000_000),
//        telescopeLevel = TelescopeLevel(13),
//        baseMineRate = xx,
//        distance = 87,
//        oreDistribution = listOf(0.60 of OSMIUM, 0.40 of QUADIUM)
//    ),
//    NARAKA(
//        unlockPrice = Price(2_500_000_000_000_000),
//        telescopeLevel = TelescopeLevel(13),
//        baseMineRate = xx,
//        distance = xx,
//        oreDistribution = listOf(1.0 of QUADIUM)
//    ),
//    DAEDALUS(
//        unlockPrice = Price(6_200_000_000_000_000),
//        telescopeLevel = TelescopeLevel(13),
//        baseMineRate = xx,
//        distance = xx,
//        oreDistribution = listOf(0.60 of QUADIUM, 0.25 of INERTON, 0.15 of SCRITH)
//    );

    private val index: Int = ordinal + 1
    private val baseUpgradeCost: Price = unlockPrice * 0.05

    fun upgradeCost(currentLevel: Int) = baseUpgradeCost * 1.3.pow(currentLevel - 1)

    override fun toString(): String = "$index.$name"
}
