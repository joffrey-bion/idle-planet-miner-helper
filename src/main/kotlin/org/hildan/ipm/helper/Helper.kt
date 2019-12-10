package org.hildan.ipm.helper

fun main() {

    val shipsBonus =
            Ships.AURORA +
            Ships.DAUGHTERSHIP +
            Ships.ELDERSHIP +
            Ships.NO_ADS

    val roomsBonus =
            Room.ENGINEERING.bonus(13) +
            Room.AERONAUTICAL.bonus(6) +
            Room.PACKAGING.bonus(3) +
            Room.FORGE.bonus(10)

    val longTermBonus = shipsBonus + roomsBonus

    val beaconBonus = Beacon.bonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus.of(1.24, 1.0, 1.0)
    )

    val lukas = Manager("Lukas", PlanetBonus.of(mineRate = 2.5), Bonus.production(smeltSpeed = 1.1))
    val angela = Manager("Angela", PlanetBonus.of(cargo = 4.0), Bonus.allPlanets(shipSpeed = 1.2))
    val nicole = Manager("Nicole", PlanetBonus.of(cargo = 2.0))

    val managerAssignment = ManagerAssignment(
        mapOf(
            PlanetType.ANADIUS to lukas,
            PlanetType.DHOLEN to angela,
            PlanetType.DRASTA to nicole
        )
    )

    val galaxy = Galaxy(longTermBonus, beaconBonus, managerAssignment)
        .withLevels(PlanetType.BALOR, 50, 30, 20)
        .withLevels(PlanetType.ANADIUS, 1, 1, 1)
        .withLevels(PlanetType.DHOLEN, 1, 1, 1)
        .withLevels(PlanetType.DRASTA, 1, 1, 1)
        .withLevels(PlanetType.VERR, 1, 1, 1)
        .withColony(PlanetType.BALOR, 4, PlanetBonus.of(mineRate = 2.0))
        .withProject(Project.BEACON)

    println(galaxy)
}
