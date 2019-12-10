package org.hildan.ipm.helper

inline class Multiplier(private val factor: Double) {

    operator fun plus(other: Multiplier) = Multiplier(factor + (other.factor - 1))

    operator fun times(other: Multiplier) = Multiplier(factor * other.factor)

    fun applyTo(value: Double): Double = value * factor

    companion object {
        val NONE = Multiplier(1.0)
    }
}

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

data class Bonus(
    val allPlanets: PlanetBonus = PlanetBonus.NONE,
    val perPlanet: EMap<PlanetType, PlanetBonus> = EMap.of { PlanetBonus.NONE },
    val production: ProductionBonus = ProductionBonus.NONE,
    val projectCostMultiplier: Multiplier = Multiplier.NONE,
    val planetUpgradeCostMultiplier: Multiplier = Multiplier.NONE,
    val planetUpgradeCostMultiplierPerColonyLevel: Multiplier = Multiplier.NONE
) {
    fun forPlanet(planet: PlanetType) = allPlanets * perPlanet[planet]

    operator fun plus(other: Bonus): Bonus = when {
        this === NONE -> other
        other === NONE -> this
        else -> Bonus(
            allPlanets = allPlanets * other.allPlanets,
            perPlanet = EMap.of { perPlanet[it] * other.perPlanet[it] },
            production = production * other.production,
            projectCostMultiplier = projectCostMultiplier * other.projectCostMultiplier,
            planetUpgradeCostMultiplier = planetUpgradeCostMultiplier * other.planetUpgradeCostMultiplier,
            planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel * other.planetUpgradeCostMultiplierPerColonyLevel
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
    }
}
