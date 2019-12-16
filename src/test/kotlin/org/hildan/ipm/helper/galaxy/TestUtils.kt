package org.hildan.ipm.helper.galaxy

object ConstantBonusesSamples {

    private val shipsBonus = Ships.DAUGHTERSHIP + Ships.ELDERSHIP + Ships.NO_ADS

    private val roomsBonus1 =
            Room.ENGINEERING.bonus(13) +
                    Room.AERONAUTICAL.bonus(6) +
                    Room.PACKAGING.bonus(3) +
                    Room.FORGE.bonus(12) +
                    Room.WORKSHOP.bonus(9) +
                    Room.ASTRONOMY.bonus(7) +
                    Room.LABORATORY.bonus(4)

    private val beaconBonus1 = Beacon.bonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus.of(1.26, 1.0, 1.0)
    )

    val SAMPLE_1 = ConstantBonuses(shipsBonus, roomsBonus1, beaconBonus1, ManagerAssignment(), Market())

}

object ProjectSamples {

    val BASIC_PROJECTS = listOf(
        Project.ASTEROID_MINER,
        Project.MANAGEMENT,
        Project.SMELTER,
        Project.ROVER
    )

    val ADVANCED_GATHERING_PROJECTS = listOf(
        Project.ADVANCED_MINING,
        Project.ADVANCED_THRUSTERS,
        Project.ADVANCED_CARGO_HANDLING,
        Project.BEACON,
        Project.ORE_TARGETING
    )

//    val ADVANCED_SMELTING_PROJECTS = listOf(
//        Project.ADVANCED_FURNACE,
//        Project.SMELTING_EFFICIENCY
//    )
}

fun assertDoubleEquals(expected: Double, actual: Double, message: String? = null) {
    org.junit.Assert.assertEquals(message, expected, actual, 0.01)
}
