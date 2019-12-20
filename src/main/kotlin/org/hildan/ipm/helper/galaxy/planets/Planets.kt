package org.hildan.ipm.helper.galaxy.planets

import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.money.min
import org.hildan.ipm.helper.galaxy.resources.OreType

data class OreRate(val oreType: OreType, val rate: Rate)

data class PlanetStats(
    val mineRate: Rate,
    val shipSpeed: Rate,
    val cargo: Double
) {
    fun deliveryRateByOreType(planet: Planet, oreTargeting: Boolean): List<OreRate> {
        val removalRate = shipSpeed * cargo / (planet.type.distance * 2.0)
        val mineRates = mineRateByType(oreTargeting, planet)
        return computeDeliveryRates(removalRate, mineRates)
    }

    private fun mineRateByType(oreTargeting: Boolean, planet: Planet): List<OreRate> {
        val getRatio = { it: OrePart ->
            if (oreTargeting && it.oreType == planet.preferredOreType) {
                it.ratio + 0.15
            } else {
                it.ratio
            }
        }
        val (preferred, others) = planet.type.oreDistribution
                    .map { OreRate(it.oreType, mineRate * getRatio(it)) }
                    .partition { it.oreType == planet.preferredOreType }
        return preferred + others.sortedByDescending { it.oreType }
    }

    private fun computeDeliveryRates(totalRemovalRate: Rate, mineRates: List<OreRate>): List<OreRate> {
        var remainingRemovalRate = totalRemovalRate

        val deliveryRates = mutableListOf<OreRate>()
        for (mr in mineRates) {
            val deliveryRate = min(remainingRemovalRate, mr.rate)
            remainingRemovalRate -= deliveryRate
            deliveryRates.add(OreRate(mr.oreType, deliveryRate))
        }
        return deliveryRates
    }
}

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
    val stats = colonyBonus.applyTo(
        PlanetStats(
            mineRate = Rate(computeStat(0.25, 0.1, 0.017, mineLevel)),
            shipSpeed = Rate(computeStat(1.0, 0.2, 1.0 / 75, shipLevel)),
            cargo = computeStat(5.1, 2.0, 0.1, cargoLevel)
        )
    )

    private fun computeStat(base: Double, c1: Double, c2: Double, level: Int) =
            base + c1 * (level - 1) + (c2 * (level - 1) * (level - 1))

    val upgradeCosts = PlanetUpgradeCosts(
        mineUpgrade = type.upgradeCost(mineLevel),
        shipUpgrade = type.upgradeCost(shipLevel),
        cargoUpgrade = type.upgradeCost(cargoLevel)
    )
}
