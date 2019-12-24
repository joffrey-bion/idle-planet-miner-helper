package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.bonuses.BeaconPlanetRange
import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.Manager
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Upgrade
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.optimizer.AppliedAction
import org.hildan.ipm.helper.optimizer.Input
import org.hildan.ipm.helper.optimizer.Optimizer
import org.hildan.ipm.helper.optimizer.compact
import java.time.Duration
import kotlin.system.measureTimeMillis

fun main() {
    val lukas = Manager("Lukas", PlanetBonus.of(mineRate = 2.5), Bonus.production(smeltSpeed = 1.1))
    val angela = Manager("Angela", PlanetBonus.of(cargo = 4.0), Bonus.allPlanets(shipSpeed = 1.2))
    val nicole = Manager("Nicole", PlanetBonus.of(cargo = 2.0))

    val input = Input(
        upgrades = listOf(Upgrade.NO_ADS, Upgrade.DAUGHTERSHIP, Upgrade.ELDERSHIP),
        mothershipRoomLevels = mapOf(
            Room.ENGINEERING to 15,
            Room.AERONAUTICAL to 6,
            Room.PACKAGING to 4,
            Room.FORGE to 13,
            Room.ASTRONOMY to 7,
            Room.LABORATORY to 6
        ),
        beacon = mapOf(
            BeaconPlanetRange.RANGE_1_4 to PlanetBonus.of(1.30, 1.0, 1.0),
            BeaconPlanetRange.RANGE_5_7 to PlanetBonus.of(1.22, 1.0, 1.0),
            BeaconPlanetRange.RANGE_8_10 to PlanetBonus.of(1.20, 1.0, 1.0)
        ),
        assignedManagers = mapOf(
            PlanetType.ANADIUS to lukas,
            PlanetType.DHOLEN to angela,
            PlanetType.DRASTA to nicole
        ),
        market = mapOf(
            AlloyType.IRON_BAR to 2.0,
            OreType.SILVER to 2.0,
            ItemType.HAMMER to 0.5
        ),
        stars = mapOf(
            OreType.SILVER to 2,
            OreType.GOLD to 1,
            AlloyType.COPPER_BAR to 1,
            AlloyType.IRON_BAR to 1,
            AlloyType.ALUMINUM_BAR to 1
        ),
        researchedProjects = listOf(
            //            Project.ASTEROID_MINER,
            //            Project.MANAGEMENT,
            //            Project.SMELTER,
            //            Project.ROVER,
            //            Project.CRAFTER,
            //            Project.TELESCOPE_1,
            //            Project.BEACON,
            //            Project.ADVANCED_MINING,
            //            Project.ADVANCED_THRUSTERS,
            //            Project.ADVANCED_CARGO_HANDLING
        ),
        planets = listOf()
    )

    //    println(galaxy)

    val time = measureTimeMillis {
        var gameTime: Duration = Duration.ZERO
        Optimizer(input.galaxy)
            .generateActions()
            .compact()
            .take(500)
            .forEachIndexed { i, action ->
                gameTime += action.time
                println(formatAction(i, gameTime, action))
            }
    }
    println("Executed in ${Duration.ofMillis(time).format()}")
}

private fun formatAction(index: Int, gameTime: Duration, action: AppliedAction): String {
    val formattedIndex = (index + 1).leftPadded(3, false)
    return "$formattedIndex. [${gameTime.format()}]  ${action.action}"
}

private fun Duration.format(): String {
    val sec = toSecondsPart().leftPadded(2)
    val min = toMinutesPart().leftPadded(2)
    val hours = toHours().leftPadded(2)
    return "${hours}:${min}:${sec}"
}

private fun Number.leftPadded(width: Int, zeroes: Boolean = true): String {
    val zero = if (zeroes) "0" else ""
    return String.format("%$zero${width}d", this)
}
