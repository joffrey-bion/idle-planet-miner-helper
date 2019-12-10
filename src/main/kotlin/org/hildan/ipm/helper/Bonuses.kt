package org.hildan.ipm.helper

private fun Double.scaleBonus(factor: Double) = (this - 1) * factor + 1

data class PlanetBonus(
    val mineRate: Double = 1.0,
    val shipSpeed: Double = 1.0,
    val cargo: Double = 1.0
) {
    operator fun times(other: PlanetBonus): PlanetBonus = PlanetBonus(
        mineRate = mineRate * other.mineRate,
        shipSpeed = shipSpeed * other.shipSpeed,
        cargo = cargo * other.cargo
    )

    fun scale(factor: Double): PlanetBonus = PlanetBonus(
        mineRate = mineRate.scaleBonus(factor),
        shipSpeed = shipSpeed.scaleBonus(factor),
        cargo = cargo.scaleBonus(factor)
    )

    companion object {
        val NONE = PlanetBonus()
    }
}

data class ProductionBonus(
    val smeltSpeed: Double = 1.0,
    val craftSpeed: Double = 1.0
) {
    operator fun times(other: ProductionBonus): ProductionBonus = ProductionBonus(
        smeltSpeed = smeltSpeed * other.smeltSpeed,
        craftSpeed = craftSpeed * other.craftSpeed
    )

    fun scale(factor: Double): ProductionBonus = ProductionBonus(
        smeltSpeed = smeltSpeed.scaleBonus(factor),
        craftSpeed = craftSpeed.scaleBonus(factor)
    )

    companion object {
        val NONE = ProductionBonus()
    }
}

data class Bonus(
    val allPlanets: PlanetBonus = PlanetBonus.NONE,
    val perPlanet: Map<PlanetType, PlanetBonus> = PlanetType.mapTo { PlanetBonus.NONE },
    val production: ProductionBonus = ProductionBonus.NONE,
    val managersMultiplier: Double = 1.0,
    val projectCostMultiplier: Double = 1.0,
    val planetUpgradeCostMultiplier: Double = 1.0,
    val planetUpgradeCostMultiplierPerColonyLevel: Double = 1.0
) {
    fun forPlanet(planet: PlanetType) = allPlanets * (perPlanet[planet] ?: PlanetBonus.NONE)

    operator fun plus(other: Bonus): Bonus = when {
        this === NONE -> other
        other === NONE -> this
        else -> Bonus(
            allPlanets = allPlanets * other.allPlanets,
            perPlanet = PlanetType.mapTo { perPlanet[it]!! * other.perPlanet[it]!! },
            production = production * other.production,
            managersMultiplier = managersMultiplier * other.managersMultiplier,
            projectCostMultiplier = projectCostMultiplier * other.projectCostMultiplier,
            planetUpgradeCostMultiplier = planetUpgradeCostMultiplier * other.planetUpgradeCostMultiplier,
            planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel * other.planetUpgradeCostMultiplierPerColonyLevel
        )
    }

    fun scale(factor: Double): Bonus = Bonus(
        allPlanets = allPlanets.scale(factor),
        perPlanet = perPlanet.mapValues { (_, b) -> b.scale(factor) },
        production = production.scale(factor),
        managersMultiplier = managersMultiplier.scaleBonus(factor),
        projectCostMultiplier = projectCostMultiplier.scaleBonus(factor),
        planetUpgradeCostMultiplier = planetUpgradeCostMultiplier.scaleBonus(factor),
        planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel.scaleBonus(factor)
    )

    companion object {
        val NONE = Bonus()

        fun allPlanets(mineRate: Double = 1.0, shipSpeed: Double = 1.0, cargo: Double = 1.0) = Bonus(
            allPlanets = PlanetBonus(
                mineRate = mineRate,
                shipSpeed = shipSpeed,
                cargo = cargo
            )
        )

        fun production(smeltSpeed: Double = 1.0, craftSpeed: Double = 1.0) = Bonus(
            production = ProductionBonus(
                smeltSpeed = smeltSpeed,
                craftSpeed = craftSpeed
            )
        )
    }
}
