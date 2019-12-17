package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.planets.PlanetStats
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.planets.PlanetUpgradeCosts
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.ResourceType
import org.hildan.ipm.helper.utils.EMap
import org.hildan.ipm.helper.utils.mergedWith

data class PlanetBonus(
    val mineRate: Multiplier = Multiplier.NONE,
    val shipSpeed: Multiplier = Multiplier.NONE,
    val cargo: Multiplier = Multiplier.NONE
) {
    operator fun times(other: PlanetBonus): PlanetBonus = PlanetBonus(
        mineRate = mineRate * other.mineRate,
        shipSpeed = shipSpeed * other.shipSpeed,
        cargo = cargo * other.cargo
    )

    fun applyTo(planetStats: PlanetStats) = PlanetStats(
        mineRate = mineRate.applyTo(planetStats.mineRate),
        shipSpeed = shipSpeed.applyTo(planetStats.shipSpeed),
        cargo = cargo.applyTo(planetStats.cargo)
    )

    companion object {
        val NONE = PlanetBonus()

        fun of(mineRate: Double = 1.0, shipSpeed: Double = 1.0, cargo: Double = 1.0) = PlanetBonus(
            mineRate = Multiplier(mineRate),
            shipSpeed = Multiplier(shipSpeed),
            cargo = Multiplier(cargo)
        )
    }
}

data class ProductionBonus(
    val smeltSpeed: Multiplier = Multiplier.NONE,
    val craftSpeed: Multiplier = Multiplier.NONE
) {
    operator fun times(other: ProductionBonus): ProductionBonus = ProductionBonus(
        smeltSpeed = smeltSpeed * other.smeltSpeed,
        craftSpeed = craftSpeed * other.craftSpeed
    )

    companion object {
        val NONE = ProductionBonus()

        fun of(smeltSpeed: Double = 1.0, craftSpeed: Double = 1.0) = ProductionBonus(
            smeltSpeed = Multiplier(smeltSpeed),
            craftSpeed = Multiplier(craftSpeed)
        )
    }
}

data class ResourceValuesBonus(
    val alloysMultiplier: Multiplier = Multiplier.NONE,
    val itemsMultiplier: Multiplier = Multiplier.NONE,
    val resourceMultipliers: Map<ResourceType, Multiplier> = emptyMap()
) {
    val totalMultiplier: Map<ResourceType, Multiplier> = ResourceType.all().associateWith { getValue(it) }

    operator fun times(other: ResourceValuesBonus) = ResourceValuesBonus(
        alloysMultiplier = alloysMultiplier * other.alloysMultiplier,
        itemsMultiplier = itemsMultiplier * other.itemsMultiplier,
        resourceMultipliers = resourceMultipliers.mergedWith(other.resourceMultipliers, Multiplier::times)
    )

    private fun getValue(resourceType: ResourceType): Multiplier {
        val mult = resourceMultipliers[resourceType] ?: Multiplier.NONE
        val typeMult = when (resourceType) {
            is AlloyType -> alloysMultiplier
            is ItemType -> itemsMultiplier
            else -> Multiplier.NONE
        }
        return mult * typeMult
    }

    companion object {
        val NONE = ResourceValuesBonus()
    }
}

data class Bonus(
    val allPlanets: PlanetBonus = PlanetBonus.NONE,
    val perPlanet: EMap<PlanetType, PlanetBonus> = EMap.of { PlanetBonus.NONE },
    val production: ProductionBonus = ProductionBonus.NONE,
    val values: ResourceValuesBonus = ResourceValuesBonus.NONE,
    val projectCostMultiplier: Multiplier = Multiplier.NONE,
    val planetUpgradeCostMultiplier: Multiplier = Multiplier.NONE,
    val planetUpgradeCost5pReductions: Int = 0
) {
    fun forPlanet(planet: PlanetType): PlanetBonus = allPlanets * perPlanet[planet]

    fun reduceUpgradeCosts(costs: PlanetUpgradeCosts, colonyLevel: Int): PlanetUpgradeCosts {
        val colonyMultiplier = Multiplier(0.95).repeat(colonyLevel).pow(planetUpgradeCost5pReductions)
        val multiplier = planetUpgradeCostMultiplier * colonyMultiplier
        return PlanetUpgradeCosts(
            mineUpgrade = multiplier.applyTo(costs.mineUpgrade),
            shipUpgrade = multiplier.applyTo(costs.shipUpgrade),
            cargoUpgrade = multiplier.applyTo(costs.cargoUpgrade)
        )
    }

    operator fun plus(other: Bonus): Bonus = when {
        this === NONE -> other
        other === NONE -> this
        else -> Bonus(
            allPlanets = allPlanets * other.allPlanets,
            perPlanet = EMap.of { perPlanet[it] * other.perPlanet[it] },
            production = production * other.production,
            values = values * other.values,
            projectCostMultiplier = projectCostMultiplier * other.projectCostMultiplier,
            planetUpgradeCostMultiplier = planetUpgradeCostMultiplier * other.planetUpgradeCostMultiplier,
            planetUpgradeCost5pReductions = planetUpgradeCost5pReductions + other.planetUpgradeCost5pReductions
        )
    }

    companion object {
        val NONE = Bonus()

        fun allPlanets(mineRate: Double = 1.0, shipSpeed: Double = 1.0, cargo: Double = 1.0) = Bonus(
            allPlanets = PlanetBonus.of(mineRate, shipSpeed, cargo)
        )

        fun production(smeltSpeed: Double = 1.0, craftSpeed: Double = 1.0) = Bonus(
            production = ProductionBonus.of(smeltSpeed, craftSpeed)
        )

        fun values(alloysMultiplier: Double = 1.0, itemsMultiplier: Double = 1.0) = Bonus(
            values = ResourceValuesBonus(
                alloysMultiplier = Multiplier(alloysMultiplier),
                itemsMultiplier = Multiplier(itemsMultiplier)
            )
        )
    }
}
