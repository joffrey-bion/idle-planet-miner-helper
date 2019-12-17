package org.hildan.ipm.helper.galaxy.planets

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
    );

    private val baseUpgradeCost: Price = unlockPrice * 0.05

    fun upgradeCost(currentLevel: Int) = baseUpgradeCost * 1.3.pow(currentLevel - 1)
}
// Regex for table: \d+\t((\w+\s)*\w+)\t(\S+)\t(\S+)\t((\w+,\s?)*\w+)\t\S+\t(\S+)\t(\S+)

// No.	Planet	UnlockCost	TelescopeTierRequired	Resources	Yield(%)	DistanceReal	BaseMineRate
//1	Balor	100	―	Copper	100	10.00	0.25
//2	Drasta	200	―	Copper,Iron 80,20	12.00	0.37
//3	Anadius	500	―	Copper,Iron	50,50	14.00	0.52
//4	Dholen	1250	―	Iron,Lead	80,20	15.00	0.70
//5	Verr	5k	1	Lead,Iron,Copper	50,30,20	16.00	0.92
//6	Newton	9k	1	Lead	100	18.00	1.18
//7	Widow	15k	1	Iron,Copper,Silica	40,40,20	20.00	1.46
//8	Acheron	25k	2	Silica,Copper	60,40	22.00	1.78
//9	Yangtze	40k	2	Silica,Aluminum	80,20	23.00	2.14
//10	Solveig	75k	2	Aluminium,Silica,Lead	50,30,20	25.00	2.53
//11	Imir	150k	3	Aluminium	100	26.00	2.95
//12	Relic	250k	3	Lead,Silica,Silver	45,35,20	28.00	3.41
//13	Nith	400k	3	Silver,Aluminium	80,20	30.00	3.90
//14	Batalla	800k	4	Copper,Iron,Gold	40,40,20	33.00	4.42
//15	Micah	1.5m	4	Gold,Silver	50,50	35.00	4.98
//16	Pranas	3.0m	4	Gold	100	37.00	5.58
//17	Castellus	6.4m	5	Aluminum, Silica, Diamond	40, 35, 25	40.00	6.20
//18	Gorgon	12m	5	Diamond, Lead	80, 20	43.00	6.86
//19	Parnitha	25m	5	Gold, Platinum	70, 30	45.00	7.56
//20	Orisoni	50m	6	Platinum, Diamond	70, 30	48.00	8.29
//21	Theseus	100m	6	Platinum	100	51.00	9.05
//22	Zelene	200m	6	Silver, Titanium	70, 30	54.00	9.85
//23	Han	400m	7	Titanium, Diamond, Gold	70, 20, 10	57.00	10.68
//24	Strennus	800m	7	Titanium, Platinum	70, 30	58.00	11.54
//25	Osun	1.6b	7	Aluminium, Iridium	60, 40	60.00	12.44
//26	Ploitari	3.2b	8	Iridium, Diamond	50, 50	63.00	13.38
//27	Elysta	6.4b	8	Iridium	100	67.00	14.34
//28	Tikkun	12.5b	8	Iridium, Titanium, Palladium	40, 35, 25	70.00	15.34
//29	Satent	25b	9	Palladium, Titanium	60, 40	72.00	16.38
//30	Urla Rast	50b	9	Palladium, Diamond	90, 10	73.00	17.45
//31	Vular	100b	9	Palladium, Osmium	70, 30	75.00	18.55
//32	Nibiru	250b	10	Osmium, Iridium	60, 40	76.00	19.69
//33	Xena	600b	10	Osmium	100	78.00	20.86
//34	Rupert	1.5T	10	Palladium, Osmium, Rhodium	55, 30, 15	78.00	22.06
//35	Pax	4T	11	Rhodium, Platinum	50, 50	80.00	23.30
//36	Ivyra	10T	11	Rhodium	100	81.00	24.58
//37	Utrits	25T	11	Rhodium, Inerton	75, 25	82.00	25.88
//38	Doosie	62T	12	Inerton, Osmium	50, 50	84.00	27.22
//39	Zulu	160T	12	Inerton	100	84.00	28.60
//40	Unicae	400T	12	Inerton, Quadium	80, 20	85.00	30.01
//41	Dune	1q	13	Osmium, Quadium	60, 40	87.00
//42	Naraka	2.50q	13	Quadium	100
//43	Daedalus	6.20q	13	Quadium, Inerton, Scrith	60, 25, 15
//44	Clovis	15q	14	Scrith, Quadium	50, 50
//45	Zero	40q	14	Scrith	100
//46	Sotomi	100q	14	Scrith, Uru	75, 25
//47	Remidian	250q	15	Uru, Quadium	60, 40
//48	Muse	600q	15
//49	Arabis	1.50Q	15
//50	Vesna	3.80Q	16	Vibranium, Scrith	60, 40
//51	Chandra	10.00Q	16
//52	Vega	25.00Q	16
//53	Crius		17
//54	Singhana		17
//55	Zumbia		17
//56	Elysium
//57	Nyota
//58	Doral
