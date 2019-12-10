package org.hildan.ipm.helper

inline class Multiplier(private val factor: Double) {

    operator fun plus(other: Multiplier) = Multiplier(factor + (other.factor - 1))

    operator fun times(other: Multiplier) = Multiplier(factor * other.factor)

    fun scale(factor: Double) = Multiplier((this.factor - 1) * factor + 1)

    fun applyTo(value: Double): Double = value * factor

    companion object {
        val NONE = Multiplier(1.0)
    }
}

private fun Double.scaleBonus(factor: Double) = (this - 1) * factor + 1

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

    fun scale(factor: Double): PlanetBonus = PlanetBonus(
        mineRate = mineRate.scale(factor),
        shipSpeed = shipSpeed.scale(factor),
        cargo = cargo.scale(factor)
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

    fun scale(factor: Double): ProductionBonus = ProductionBonus(
        smeltSpeed = smeltSpeed.scale(factor),
        craftSpeed = craftSpeed.scale(factor)
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
    val managersBonusMultiplier: Double = 1.0,
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
            managersBonusMultiplier = managersBonusMultiplier * other.managersBonusMultiplier,
            projectCostMultiplier = projectCostMultiplier * other.projectCostMultiplier,
            planetUpgradeCostMultiplier = planetUpgradeCostMultiplier * other.planetUpgradeCostMultiplier,
            planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel * other.planetUpgradeCostMultiplierPerColonyLevel
        )
    }

    fun scale(factor: Double): Bonus = Bonus(
        allPlanets = allPlanets.scale(factor),
        perPlanet = perPlanet.mapValues { (_, b) -> b.scale(factor) }.asEMap(),
        production = production.scale(factor),
        managersBonusMultiplier = managersBonusMultiplier.scaleBonus(factor),
        projectCostMultiplier = projectCostMultiplier.scale(factor),
        planetUpgradeCostMultiplier = planetUpgradeCostMultiplier.scale(factor),
        planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel.scale(factor)
    )

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
