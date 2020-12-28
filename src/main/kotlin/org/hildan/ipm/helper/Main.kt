package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.bonuses.*
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.optimizer.AppliedAction
import org.hildan.ipm.helper.optimizer.Input
import org.hildan.ipm.helper.optimizer.Optimizer
import org.hildan.ipm.helper.optimizer.compact
import kotlin.time.Duration
import kotlin.time.measureTime

fun main() {
    val lukas = Manager("Lukas", PlanetBonus.of(mineRate = 2.5), Bonus.production(smeltSpeed = 1.1))
    val angela = Manager("Angela", PlanetBonus.of(cargo = 4.0), Bonus.allPlanets(shipSpeed = 1.2))
    val nicole = Manager("Nicole", PlanetBonus.of(cargo = 2.0))

    val input = Input(
        upgrades = listOf(
            Upgrade.NO_ADS,
            Upgrade.DAUGHTERSHIP,
            Upgrade.ELDERSHIP,
            Upgrade.MERCHANT,
            Upgrade.THUNDERHORSE,
        ),
        mothershipRoomLevels = mapOf(
            Room.ENGINEERING to 26,
            Room.AERONAUTICAL to 18,
            Room.PACKAGING to 22,
            Room.FORGE to 26,
            Room.WORKSHOP to 25,
            Room.ASTRONOMY to 11,
            Room.LABORATORY to 11,
//            Room.TERRARIUM to 11,
//            Room.LOUNGE to 26,
//            Room.BACKUP_GENERATOR to 26,
            Room.UNDERFORGE to 11,
            Room.DORMS to 11,
            Room.SALES to 20,
            Room.CLASSROOM to 19,
//            Room.MARKETING to 16,
//            Room.PLANET_RELATIONS to 18,
        ),
        beacon = listOf(
            BeaconRangeBonus(1, 40, PlanetBonus.of(1.5, 1.0, 1.0)),
//            BeaconRangeBonus(44, 46, PlanetBonus.of(1.48, 1.03, 1.0)),
//            BeaconRangeBonus(47, 58, PlanetBonus.of(1.5, 1.0, 1.0)),
        ),
        assignedManagers = mapOf(
            Planet.ANADIUS to lukas,
            Planet.DHOLEN to angela,
            Planet.DRASTA to nicole,
        ),
        market = mapOf(
            AlloyType.IRON_BAR to 2.0,
            OreType.SILVER to 2.0,
            ItemType.HAMMER to 0.5,
        ),
        stars = mapOf(
            OreType.COPPER to 20,
            OreType.IRON to 12,
            OreType.LEAD to 13,
            OreType.SILICA to 11,
            OreType.ALUMINUM to 9,
            OreType.SILVER to 10,
            OreType.GOLD to 15,
            OreType.DIAMOND to 6,
            OreType.PLATINUM to 12,
            OreType.TITANIUM to 4,
            AlloyType.COPPER_BAR to 16,
            AlloyType.IRON_BAR to 13,
            AlloyType.LEAD_BAR to 9,
            AlloyType.SILICON_BAR to 18,
            AlloyType.ALUMINUM_BAR to 12,
            AlloyType.SILVER_BAR to 12,
            AlloyType.GOLD_BAR to 9,
            AlloyType.BRONZE_BAR to 7,
            ItemType.COPPER_WIRE to 14,
            ItemType.IRON_NAIL to 15,
            ItemType.BATTERY to 10,
            ItemType.HAMMER to 15,
            ItemType.GLASS to 11,
            ItemType.CIRCUIT to 7,
            ItemType.LENSE to 7,
            ItemType.LASER to 0,
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

    val time = measureTime {
        var gameTime: Duration = Duration.ZERO
        Optimizer(input.galaxy)
            .generateActions(searchDepth = 5)
            .compact()
            .take(500)
            .forEachIndexed { i, action ->
                gameTime += action.time
                println(formatAction(i, gameTime, action))
            }
    }
    println("Executed in ${time.format()}")
}

private fun formatAction(index: Int, gameTime: Duration, action: AppliedAction): String {
    val formattedIndex = "${(index + 1)}".padStart(3, ' ')
    val incomePerMinute = action.newGalaxy.totalIncomeRate.formatPerMinute()
    return "$formattedIndex. [${gameTime.format()}]  ${action.action}\t\tNow: $incomePerMinute"
}

private fun Duration.format(): String = toComponents { hours, minutes, seconds, nanoseconds ->
    val h = "$hours".padStart(2, '0')
    val min = "$minutes".padStart(2, '0')
    val sec = "$seconds".padStart(2, '0')
    val ms = "${(nanoseconds / 1_000_000)}".padStart(3, '0')
    "${h}:${min}:${sec}.${ms}"
}
