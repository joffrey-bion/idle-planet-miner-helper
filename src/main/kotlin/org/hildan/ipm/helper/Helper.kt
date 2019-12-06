package org.hildan.ipm.helper

fun main() {

    val bonus = Ships.AURORA +
            Ships.DAUGHTERSHIP +
            Ships.ELDERSHIP +
            Room.ENGINEERING.bonus(10) +
            Room.AERONAUTICAL.bonus(4) +
            Room.FORGE.bonus(9)

    val beaconBonus = BeaconBonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus(1.25, 1.0, 1.0)
    )

    val managers = listOf(
        Manager("Lukas", PlanetBonus(mineRate = 2.5), Bonus(production = ProductionBonus(smeltSpeed = 1.1))),
        Manager("Angela", PlanetBonus(cargo = 4.0), Bonus(globalPlanetBonus = PlanetBonus(shipSpeed = 1.2))),
        Manager("Nicole", PlanetBonus(cargo = 2.0))
    )

    val galaxy = Galaxy(bonus, beaconBonus, managers)

    println(galaxy)
}
