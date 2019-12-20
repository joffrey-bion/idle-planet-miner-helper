package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.bonuses.Beacon
import org.hildan.ipm.helper.galaxy.bonuses.BeaconPlanetRange
import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ChallengeStars
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.Manager
import org.hildan.ipm.helper.galaxy.bonuses.ManagerAssignment
import org.hildan.ipm.helper.galaxy.bonuses.Market
import org.hildan.ipm.helper.galaxy.bonuses.Multiplier
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Ships
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.OreType
import java.time.Duration

fun main() {
    val shipsBonus = Ships.DAUGHTERSHIP + Ships.ELDERSHIP + Ships.NO_ADS

    val roomsBonus =
            Room.ENGINEERING.bonus(15) +
            Room.AERONAUTICAL.bonus(6) +
            Room.PACKAGING.bonus(4) +
            Room.FORGE.bonus(13) +
            Room.ASTRONOMY.bonus(7) +
            Room.LABORATORY.bonus(6)

    val beaconBonus = Beacon.bonus(
        BeaconPlanetRange.RANGE_1_4 to PlanetBonus.of(1.30, 1.0, 1.0),
        BeaconPlanetRange.RANGE_5_7 to PlanetBonus.of(1.22, 1.0, 1.0),
        BeaconPlanetRange.RANGE_8_10 to PlanetBonus.of(1.20, 1.0, 1.0)
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

    val market = Market(mapOf(
        AlloyType.IRON_BAR to Multiplier(2.0),
        OreType.SILVER to Multiplier(2.0),
        ItemType.HAMMER to Multiplier(0.5)
    ))

    val stars = ChallengeStars(mapOf(
        OreType.SILVER to 2,
        OreType.GOLD to 1,
        AlloyType.COPPER_BAR to 1,
        AlloyType.IRON_BAR to 1,
        AlloyType.ALUMINUM_BAR to 1
    ))

    val constantBonuses = ConstantBonuses(shipsBonus, roomsBonus, beaconBonus, managerAssignment, market, stars)

    val galaxy = Galaxy(constantBonuses)
        .withBoughtPlanet(PlanetType.BALOR)
        .withBoughtPlanet(PlanetType.DRASTA)
        .withBoughtPlanet(PlanetType.ANADIUS)
        .withBoughtPlanet(PlanetType.DHOLEN)
        .withBoughtPlanet(PlanetType.VERR)
        .withLevels(PlanetType.BALOR, 50, 30, 20)
        .withLevels(PlanetType.DRASTA, 40, 30, 20)
        .withLevels(PlanetType.ANADIUS, 40, 30, 20)
        .withLevels(PlanetType.DHOLEN, 40, 30, 30)
        .withLevels(PlanetType.VERR, 40, 30, 20)
        .withColony(PlanetType.BALOR, 4, PlanetBonus.of(mineRate = 2.0))
        .withColony(PlanetType.DRASTA, 3, PlanetBonus.of(mineRate = 1.75))
        .withColony(PlanetType.ANADIUS, 3, PlanetBonus.of(mineRate = 1.75))
        .withColony(PlanetType.DHOLEN, 4, PlanetBonus.of(mineRate = 2.0))
        .withColony(PlanetType.VERR, 2, PlanetBonus.of(mineRate = 1.5))
        .withProject(Project.BEACON)
        .withProject(Project.ADVANCED_MINING)
        .withProject(Project.ADVANCED_THRUSTERS)
        .withProject(Project.ADVANCED_CARGO_HANDLING)
        .withProject(Project.ORE_TARGETING)
        .withProject(Project.COLONY_TAX_INCENTIVES)
        .withProject(Project.COLONY_ADVANCED_TAX_INCENTIVES)

//    println(galaxy)

    var gameTime: Duration = Duration.ZERO
    Optimizer(Galaxy(constantBonuses))
        .generateActions()
        .compact()
        .take(600)
        .forEachIndexed { i, action ->
            gameTime += action.time
            println("$i.\t[${gameTime.format()}]\t${action.action}")
        }
}

private fun Duration.format(): String = "${toHoursPart()}h ${toMinutesPart()}m ${toSecondsPart()}s"
