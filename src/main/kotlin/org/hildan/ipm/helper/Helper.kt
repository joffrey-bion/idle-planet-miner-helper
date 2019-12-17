package org.hildan.ipm.helper

import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.bonuses.Beacon
import org.hildan.ipm.helper.galaxy.bonuses.BeaconPlanetRange
import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.bonuses.Manager
import org.hildan.ipm.helper.galaxy.bonuses.ManagerAssignment
import org.hildan.ipm.helper.galaxy.money.Market
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Ships

fun main() {
    val shipsBonus = Ships.DAUGHTERSHIP + Ships.ELDERSHIP + Ships.NO_ADS

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
        .withMultiplier(ItemType.HAMMER, 0.5)

    val constantBonuses = ConstantBonuses(shipsBonus, roomsBonus, beaconBonus, managerAssignment, market)

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

    Optimizer(Galaxy(constantBonuses)).generateActions().take(50).forEach { println(it.action) }
}
