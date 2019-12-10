package org.hildan.ipm.helper

fun main() {

    val shipsBonus =
            Ships.AURORA +
            Ships.DAUGHTERSHIP +
            Ships.ELDERSHIP

    val roomsBonus =
            Room.ENGINEERING.bonus(13) +
            Room.AERONAUTICAL.bonus(6) +
            Room.FORGE.bonus(9)

    val longTermBonus = shipsBonus + roomsBonus

    val beaconBonus = Beacon.bonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus.of(1.22, 1.0, 1.0)
    )

    val lukas = Manager("Lukas", PlanetBonus.of(mineRate = 2.5), Bonus.production(smeltSpeed = 1.1))
    val angela = Manager("Angela", PlanetBonus.of(cargo = 4.0), Bonus.allPlanets(shipSpeed = 1.2))
    val nicole = Manager("Nicole", PlanetBonus.of(cargo = 2.0))

    val galaxy = Galaxy(longTermBonus, beaconBonus)
        .withLevels(PlanetType.BALOR, 40, 19, 19)
        .withLevels(PlanetType.ANADIUS, 1, 1, 1)
        .withLevels(PlanetType.DHOLEN, 1, 1, 1)
        .withLevels(PlanetType.DRASTA, 1, 1, 1)
        .withLevels(PlanetType.VERR, 1, 1, 1)
        .withProject(Project.BEACON)
        .withManagerAssignedTo(lukas, PlanetType.ANADIUS)
        .withManagerAssignedTo(angela, PlanetType.DRASTA)
//        .withManagerAssignedTo(nicole, PlanetType.BALOR)

    println(galaxy)
}
