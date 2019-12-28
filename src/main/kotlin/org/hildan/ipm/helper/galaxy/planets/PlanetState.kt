package org.hildan.ipm.helper.galaxy.planets

import org.hildan.ipm.helper.galaxy.bonuses.Bonuses
import org.hildan.ipm.helper.galaxy.bonuses.PlanetBonus
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.money.Rate
import org.hildan.ipm.helper.galaxy.resources.OreType
import org.hildan.ipm.helper.utils.associateMerging

data class PlanetUpgradeCosts(
    val mineUpgrade: Price,
    val shipUpgrade: Price,
    val cargoUpgrade: Price
)

data class PlanetState(
    val planet: Planet,
    val mineLevel: Int = 1,
    val shipLevel: Int = 1,
    val cargoLevel: Int = 1,
    val preferredOreType: OreType = planet.oreDistribution.map { it.oreType }.maxBy { it.baseValue }!!,
    val colonyLevel: Int = 0,
    val colonyBonus: PlanetBonus = PlanetBonus.NONE,
    val galaxyBonuses: Bonuses
) {
    private val totalBonus = colonyBonus * galaxyBonuses.total.forPlanet(planet)

    val production = totalBonus.applyTo(PlanetProduction.forLevels(mineLevel, shipLevel, cargoLevel))

    val oreRates = production.deliveryRateByOreType(this, galaxyBonuses.oreTargetingActive)

    val upgradeCosts = galaxyBonuses.total.reduceUpgradeCosts(
        costs = PlanetUpgradeCosts(
            mineUpgrade = planet.upgradeCost(mineLevel),
            shipUpgrade = planet.upgradeCost(shipLevel),
            cargoUpgrade = planet.upgradeCost(cargoLevel)
        ),
        colonyLevel = colonyLevel
    )
}

data class Planets(
    val states: List<PlanetState> = emptyList()
) {
    val production = computePlanetStats()

    private fun computePlanetStats() = states.associate { it.planet to it.production }

    val upgradeCosts = computePlanetCosts()

    private fun computePlanetCosts() = states.associate { it.planet to it.upgradeCosts }

    val oreRatesByType: Map<OreType, Rate> = computeOreRates()

    private fun computeOreRates(): Map<OreType, Rate> =
            states.flatMap { it.oreRates }.associateMerging({ it.oreType }, { it.rate }, Rate::plus)

    val accessibleOres: Set<OreType>
        get() = oreRatesByType.keys

    fun withBoughtPlanet(planet: Planet, bonuses: Bonuses): Planets =
            copy(states = states + PlanetState(planet, galaxyBonuses = bonuses))

    inline fun withChangedPlanet(planet: Planet, transform: (PlanetState) -> PlanetState): Planets = copy(
        states = states.map { if (it.planet == planet) transform(it) else it }
    )

    fun withBonuses(bonuses: Bonuses) = Planets(states = states.map { it.copy(galaxyBonuses = bonuses) })
}
