package org.hildan.ipm.helper.optimizer

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.bonuses.Beacon
import org.hildan.ipm.helper.galaxy.bonuses.BeaconPlanetRange
import org.hildan.ipm.helper.galaxy.bonuses.ChallengeStars
import org.hildan.ipm.helper.galaxy.bonuses.ConstantBonuses
import org.hildan.ipm.helper.galaxy.bonuses.Manager
import org.hildan.ipm.helper.galaxy.bonuses.ManagerAssignment
import org.hildan.ipm.helper.galaxy.bonuses.Market
import org.hildan.ipm.helper.galaxy.bonuses.Multiplier
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.bonuses.Room
import org.hildan.ipm.helper.galaxy.bonuses.Rooms
import org.hildan.ipm.helper.galaxy.bonuses.Upgrade
import org.hildan.ipm.helper.galaxy.bonuses.sum
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.resources.ResourceType

data class Input(
    val upgrades: List<Upgrade>,
    val mothershipRoomLevels: Map<Room, Int>,
    val beacon: Map<BeaconPlanetRange, PlanetBonus>,
    val assignedManagers: Map<PlanetType, Manager>,
    val market: Map<ResourceType, Double>,
    val stars: Map<ResourceType, Int>,
    val researchedProjects: List<Project>,
    val planets: List<Planet>
) {
    private val constantBonuses = ConstantBonuses(
        shipsBonus = upgrades.map { it.bonus }.sum(),
        roomsBonus = Rooms.bonus(mothershipRoomLevels),
        beaconBonus = Beacon.bonus(beacon),
        managerAssignment = ManagerAssignment(assignedManagers),
        market = Market(market.mapValues { (_, f) ->
            Multiplier(f)
        }),
        stars = ChallengeStars(stars)
    )
    val galaxy = Galaxy.init(constantBonuses).withProjects(researchedProjects).withPlanets(planets)
}

private fun Galaxy.withPlanets(planets: List<Planet>) : Galaxy {
    var nextGalaxy = this
    planets.forEach {
        nextGalaxy = nextGalaxy
            .withBoughtPlanet(it.type)
            .withLevels(it.type, it.mineLevel, it.shipLevel, it.cargoLevel)
            .withColony(it.type, it.colonyLevel, it.colonyBonus)
    }
    return nextGalaxy
}
