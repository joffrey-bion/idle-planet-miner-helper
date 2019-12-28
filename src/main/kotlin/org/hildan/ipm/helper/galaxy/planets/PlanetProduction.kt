package org.hildan.ipm.helper.galaxy.planets

import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.utils.LazyMap
import org.hildan.ipm.helper.utils.lazyHashMap

data class OreRate(val oreType: OreType, val rate: Rate)

data class PlanetProduction(
    val mineRate: Rate,
    val shipSpeed: Rate,
    val cargo: Double
) {
    fun deliveryRateByOreType(planetState: PlanetState, oreTargeting: Boolean): List<OreRate> {
        val removalRate = shipSpeed * cargo / (planetState.planet.distance * 2.0)
        val mineRates = mineRateByType(oreTargeting, planetState)
        return computeDeliveryRates(removalRate, mineRates)
    }

    private fun mineRateByType(oreTargeting: Boolean, planetState: PlanetState): List<OreRate> {
        val getRatio = { it: OrePart ->
            if (oreTargeting && it.oreType == planetState.preferredOreType) {
                it.ratio + 0.15
            } else {
                it.ratio
            }
        }
        val (preferred, others) = planetState.planet.oreDistribution
            .asSequence()
            .map { OreRate(it.oreType, mineRate * getRatio(it)) }
            .partition { it.oreType == planetState.preferredOreType }
        return preferred + others.sortedByDescending { it.oreType }
    }

    private fun computeDeliveryRates(totalRemovalRate: Rate, mineRates: List<OreRate>): List<OreRate> {
        var remainingRemovalRate = totalRemovalRate

        val deliveryRates = mutableListOf<OreRate>()
        for (mr in mineRates) {
            if (remainingRemovalRate <= mr.rate) {
                deliveryRates.add(
                    OreRate(mr.oreType, remainingRemovalRate)
                )
                break
            }
            remainingRemovalRate -= mr.rate
            deliveryRates.add(mr)
        }
        return deliveryRates
    }

    companion object {
        private val cache: LazyMap<Int, LazyMap<Int, LazyMap<Int, PlanetProduction>>> = lazyHashMap { mineLevel ->
            lazyHashMap { shipLevel ->
                lazyHashMap { cargoLevel ->
                    PlanetProduction(
                        mineRate = Rate(computeStat(0.25, 0.1, 0.017, mineLevel)),
                        shipSpeed = Rate(computeStat(1.0, 0.2, 1.0 / 75, shipLevel)),
                        cargo = computeStat(5.1, 2.0, 0.1, cargoLevel)
                    )
                }
            }
        }

        private fun computeStat(base: Double, c1: Double, c2: Double, level: Int) =
                base + c1 * (level - 1) + (c2 * (level - 1) * (level - 1))

        fun forLevels(mine: Int, ship: Int, cargo: Int): PlanetProduction = cache[mine][ship][cargo]
    }
}
