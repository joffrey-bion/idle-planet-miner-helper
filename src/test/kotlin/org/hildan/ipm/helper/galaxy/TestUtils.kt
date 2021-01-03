package org.hildan.ipm.helper.galaxy

import org.hildan.ipm.helper.galaxy.bonuses.BeaconRangeBonus
import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.ManagerAssignment
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Upgrade
import org.hildan.ipm.helper.galaxy.bonuses.asSingleBonus
import kotlin.math.abs
import kotlin.test.assertTrue

object ConstantBonusesSamples {

    private val shipsBonus = Upgrade.DAUGHTERSHIP.bonus + Upgrade.ELDERSHIP.bonus + Upgrade.NO_ADS.bonus

    private val roomsBonus1 =
            Room.ENGINEERING.bonus(13) +
            Room.AERONAUTICAL.bonus(6) +
            Room.PACKAGING.bonus(3) +
            Room.FORGE.bonus(12) +
            Room.WORKSHOP.bonus(9) +
            Room.ASTRONOMY.bonus(7) +
            Room.LABORATORY.bonus(4)

    private val beaconBonus1 = listOf(BeaconRangeBonus(1, 4, PlanetBonus.of(1.26, 1.0, 1.0))).asSingleBonus()

    val NONE = ConstantBonuses(
        shipsBonus = Bonus.NONE,
        roomsBonus = Bonus.NONE,
        beaconBonus = Bonus.NONE,
        managerAssignment = ManagerAssignment(),
        marketBonus = Bonus.NONE,
        starsBonus = Bonus.NONE
    )

    val SAMPLE_1 = ConstantBonuses(
        shipsBonus, roomsBonus1, beaconBonus1, ManagerAssignment(), Bonus.NONE, Bonus.NONE
    )

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
    assertTrue(abs(expected - actual) < 0.001, message)
}
