package org.hildan.ipm.helper.galaxy.bonuses

object Ships {

    val DAUGHTERSHIP = Bonus.allPlanets(mineRate = 1.5, shipSpeed = 1.25, cargo = 1.25)

    val ELDERSHIP =
            Bonus.allPlanets(mineRate = 2.0, shipSpeed = 1.5, cargo = 1.5) +
            Bonus.production(smeltSpeed = 1.5, craftSpeed = 1.5)

    val NO_ADS = Bonus.allPlanets(mineRate = 1.2)
}

enum class Room(
    val bonus: (Int) -> Bonus
) {
    ENGINEERING({ l -> Bonus.allPlanets(mineRate = 1.25 + (l-1) * 0.15) }),
    FORGE({ l -> Bonus.production(smeltSpeed = 1.2 + (l-1) * 0.1) }),
    AERONAUTICAL({ l -> Bonus.allPlanets(shipSpeed = 1.5 + (l-1) * 0.25) }),
    ASTRONOMY({ l -> Bonus(planetUpgradeCostMultiplier = Multiplier(0.9 + (l - 1) * (-0.04))) }),
    PACKAGING({ l -> Bonus.allPlanets(cargo = 1.5 + (l - 1) * 0.25) }),
    WORKSHOP({ l -> Bonus.production(craftSpeed = 1.1 + (l - 1) * 0.1) }),
    LABORATORY({ l -> Bonus(projectCostMultiplier = Multiplier(0.9 + (l - 1) * (-0.04))) }),
}

/*
Room	Boost	Min cost	Combined min cost   BaseEffect  PerLevel    MaxLevel

Engineering	Increase mine speed	4	4	X1.25	+0.15	40
Forge	Increase smelt speed	4	4	X1.1	+0.1	40
Aeronautical	Increase ship speed	6	10	X1.5	+0.25	40
Astronomy	Reduce planet upgrade prices	12	22	X0.9	-0.04	11
Packaging	Increase cargo	21	43	X1.5	+0.25	40
Workshop	Increase craft speed	35	78	X1.1	+0.1	4
Laboratory	Decrease project cost	56	134	X0.9	-0.04	11
Robotics	Decrease rover time	87	221	X0.9
Lounge	Increase credits earned	133	354	X1.15
Backup Generator	Increase max idle time	200	554
Terrarium	Decrease colonization cost	298	852
Underforge	Decrease smelter ingredients	439	1291
Dorm	Decrease crafter ingredients	642	1933
Sales	Increase alloy and item value	934	2867
Classroom	All manager bonuses	1351	4218
Marketing	Increase Market Bonuses	-	-
 */
