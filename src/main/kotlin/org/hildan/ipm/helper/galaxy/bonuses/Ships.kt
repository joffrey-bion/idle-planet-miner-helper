package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.resources.ResourceType

enum class Upgrade(
    val bonus: Bonus
) {
    NO_ADS(Bonus.allPlanets(mineRate = 1.2)),
    DAUGHTERSHIP(Bonus.allPlanets(mineRate = 1.5, shipSpeed = 1.25, cargo = 1.25)),
    ELDERSHIP(
        Bonus.allPlanets(mineRate = 2.0, shipSpeed = 1.5, cargo = 1.5) +
        Bonus.production(smeltSpeed = 1.5, craftSpeed = 1.5)
    ),
    THUNDERHORSE(Bonus.production(smeltSpeed = 2.0, craftSpeed = 2.0)),
    MERCHANT(Bonus.values(multipliers = ResourceType.ALL.associateWith { 2.0 })),
}

fun Map<Room, Int>.asSingleBonus() = map { (room, level) -> room.bonus(level) }.sum()

enum class Room(
    val bonus: (level: Int) -> Bonus
) {
    ENGINEERING({ l -> Bonus.allPlanets(mineRate = 1.25 + (l-1) * 0.15) }),
    FORGE({ l -> Bonus.production(smeltSpeed = 1.2 + (l-1) * 0.1) }),
    AERONAUTICAL({ l -> Bonus.allPlanets(shipSpeed = 1.5 + (l-1) * 0.25) }),
    ASTRONOMY({ l -> Bonus(planetUpgradeCostMultiplier = Multiplier(0.9 + (l - 1) * (-0.04))) }),
    PACKAGING({ l -> Bonus.allPlanets(cargo = 1.5 + (l - 1) * 0.25) }),
    WORKSHOP({ l -> Bonus.production(craftSpeed = 1.1 + (l - 1) * 0.1) }),
    LABORATORY({ l -> Bonus(projectCostMultiplier = Multiplier(0.9 + (l - 1) * (-0.04))) }),
    UNDERFORGE({ l -> Bonus.production(smeltIngredients = 0.9 + (l - 1) * (-0.04)) }),
    DORMS({ l -> Bonus.production(craftIngredients = 0.9 + (l - 1) * -0.04) }),
    SALES({ l -> (1.1 + (l-1) * 0.05).let { m -> Bonus.values(alloysMultiplier = m, itemsMultiplier = m) }}),
    CLASSROOM({ l -> Bonus.NONE }), // TODO
}

/*
Room	Boost	Min cost	Combined min cost   BaseEffect  PerLevel    MaxLevel

Robotics	Decrease rover time	87	221	X0.9
Lounge	Increase credits earned	133	354	X1.15
Backup Generator	Increase max idle time	200	554
Terrarium	Decrease colonization cost	298	852
Sales	Increase alloy and item value	934	2867
Classroom	All manager bonuses	1351	4218
Marketing	Increase Market Bonuses	-	-
 */
