package org.hildan.ipm.helper

fun main() {

    val longTermBonus = Ships.AURORA +
            Ships.DAUGHTERSHIP +
            Ships.ELDERSHIP +
            Room.ENGINEERING.bonus(10) +
            Room.AERONAUTICAL.bonus(4) +
            Room.FORGE.bonus(9)

    val beaconBonus = BeaconBonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus(1.25, 1.0, 1.0)
    )

    val lukas = Manager("Lukas", PlanetBonus(mineRate = 2.5), Bonus(production = ProductionBonus(smeltSpeed = 1.1)))
    val angela = Manager("Angela", PlanetBonus(cargo = 4.0), Bonus(globalPlanetBonus = PlanetBonus(shipSpeed = 1.2)))
    val nicole = Manager("Nicole", PlanetBonus(cargo = 2.0))

    val galaxy = Galaxy(longTermBonus, beaconBonus)
        .withLevels(PlanetType.BALOR, 1, 1, 1)
        .withLevels(PlanetType.ANADIUS, 1, 1, 1)
        .withLevels(PlanetType.DHOLEN, 1, 1, 1)
        .withLevels(PlanetType.DRASTA, 1, 1, 1)
        .withLevels(PlanetType.VERR, 1, 1, 1)
        .withManagerAssignedTo(lukas, PlanetType.BALOR)
        .withManagerAssignedTo(angela, PlanetType.BALOR)
        .withManagerAssignedTo(nicole, PlanetType.BALOR)

    println(galaxy)
}
