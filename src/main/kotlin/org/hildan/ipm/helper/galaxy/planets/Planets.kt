package org.hildan.ipm.helper.galaxy.planets

import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.resources.OreType

data class PlanetUpgradeCosts(
    val mineUpgrade: Price,
    val shipUpgrade: Price,
    val cargoUpgrade: Price
)

data class Planet(
    val type: PlanetType,
    val mineLevel: Int = 1,
    val shipLevel: Int = 1,
    val cargoLevel: Int = 1,
    val preferredOreType: OreType = type.oreDistribution.map { it.oreType }.maxBy { it.baseValue }!!,
    val colonyLevel: Int = 0,
    val colonyBonus: PlanetBonus = PlanetBonus.NONE
) {
    val stats = colonyBonus.applyTo(PlanetStats.forLevels(mineLevel, shipLevel, cargoLevel))

    val upgradeCosts = PlanetUpgradeCosts(
        mineUpgrade = type.upgradeCost(mineLevel),
        shipUpgrade = type.upgradeCost(shipLevel),
        cargoUpgrade = type.upgradeCost(cargoLevel)
    )
}
