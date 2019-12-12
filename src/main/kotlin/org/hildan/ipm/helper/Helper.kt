package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.AlloyType
import org.hildan.ipm.helper.galaxy.Beacon
import org.hildan.ipm.helper.galaxy.BeaconPlanetRange
import org.hildan.ipm.helper.galaxy.Bonus
import org.hildan.ipm.helper.galaxy.ConstantBonuses
import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Item
import org.hildan.ipm.helper.galaxy.Manager
import org.hildan.ipm.helper.galaxy.ManagerAssignment
import org.hildan.ipm.helper.galaxy.Market
import org.hildan.ipm.helper.galaxy.OreType
import org.hildan.ipm.helper.galaxy.PlanetBonus
import org.hildan.ipm.helper.galaxy.PlanetType
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.Room
import org.hildan.ipm.helper.galaxy.Ships

fun main() {
    val shipsBonus =
            Ships.DAUGHTERSHIP +
            Ships.ELDERSHIP +
            Ships.NO_ADS

    val roomsBonus =
            Room.ENGINEERING.bonus(13) +
            Room.AERONAUTICAL.bonus(6) +
            Room.PACKAGING.bonus(3) +
            Room.FORGE.bonus(10) +
            Room.ASTRONOMY.bonus(7)

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

    val market = Market()
        .withStars(OreType.SILVER, 2)
        .withStars(OreType.GOLD, 1)
        .withStars(AlloyType.COPPER_BAR, 1)
        .withStars(AlloyType.IRON_BAR, 1)
        .withStars(AlloyType.ALUMINUM_BAR, 1)
        .withMultiplier(AlloyType.IRON_BAR, 2.0)
        .withMultiplier(OreType.SILVER, 2.0)
        .withMultiplier(Item.HAMMER, 0.5)

    val constantBonuses = ConstantBonuses(shipsBonus, roomsBonus, beaconBonus, managerAssignment, market)

    println(constantBonuses)

    val galaxy = Galaxy(constantBonuses)
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

    println(galaxy)
}
