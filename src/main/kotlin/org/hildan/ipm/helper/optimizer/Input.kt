package org.hildan.ipm.helper.optimizer

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.bonuses.BeaconRangeBonus
import org.hildan.ipm.helper.galaxy.bonuses.Bonus
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.Manager
import org.hildan.ipm.helper.galaxy.bonuses.ManagerAssignment
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Upgrade
import org.hildan.ipm.helper.galaxy.bonuses.asSingleBonus
import org.hildan.ipm.helper.galaxy.bonuses.sum
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.planets.PlanetState
import org.hildan.ipm.helper.galaxy.resources.ResourceType

data class Input(
    val upgrades: List<Upgrade>,
    val mothershipRoomLevels: Map<Room, Int>,
    val beacon: List<BeaconRangeBonus>,
    val assignedManagers: Map<Planet, Manager>,
    val market: Map<ResourceType, Double>,
    val stars: Map<ResourceType, Int>,
    val researchedProjects: List<Project>,
    val planets: List<PlanetState>,
) {
    private val constantBonuses = ConstantBonuses(
        shipsBonus = upgrades.map { it.bonus }.sum(),
        roomsBonus = mothershipRoomLevels.asSingleBonus(),
        beaconBonus = beacon.asSingleBonus(),
        managerAssignment = ManagerAssignment(assignedManagers),
        marketBonus = Bonus.values(market),
        starsBonus = Bonus.values(stars.mapValues { 1 + 0.2 * it.value }),
    )
    val galaxy = Galaxy.init(constantBonuses).withProjects(researchedProjects).withPlanets(planets)
}

private fun Galaxy.withPlanets(planets: List<PlanetState>) : Galaxy {
    var nextGalaxy = this
    planets.forEach {
        nextGalaxy = nextGalaxy
            .withBoughtPlanet(it.planet)
            .withLevels(it.planet, it.mineLevel, it.shipLevel, it.cargoLevel)
            .withColony(it.planet, it.colonyLevel, it.colonyBonus)
    }
    return nextGalaxy
}
