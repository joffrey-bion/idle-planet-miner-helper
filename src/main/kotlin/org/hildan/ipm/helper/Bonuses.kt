package org.hildan.ipm.helper

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
}

data class Bonus(
    val globalPlanetBonus: PlanetBonus = PlanetBonus.NONE,
    val production: ProductionBonus = ProductionBonus(),
    val managersMultiplier: Double = 1.0,
    val projectCostMultiplier: Double = 1.0,
    val planetUpgradeCostMultiplier: Double = 1.0,
    val planetUpgradeCostMultiplierPerColonyLevel: Double = 1.0
) {
    operator fun plus(other: Bonus): Bonus = when {
        this === NONE -> other
        other === NONE -> this
        else -> Bonus(
            globalPlanetBonus = globalPlanetBonus * other.globalPlanetBonus,
            production = production * other.production,
            managersMultiplier = managersMultiplier * other.managersMultiplier,
            projectCostMultiplier = projectCostMultiplier * other.projectCostMultiplier,
            planetUpgradeCostMultiplier = planetUpgradeCostMultiplier * other.planetUpgradeCostMultiplier,
            planetUpgradeCostMultiplierPerColonyLevel = planetUpgradeCostMultiplierPerColonyLevel * other.planetUpgradeCostMultiplierPerColonyLevel
        )
    }

    companion object {
        val NONE = Bonus()
    }
}
