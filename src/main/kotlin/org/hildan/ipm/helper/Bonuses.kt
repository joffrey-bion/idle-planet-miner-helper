package org.hildan.ipm.helper

val NO_BONUS = Bonus()

data class PlanetBonus(
    val miningRate: Double = 1.0,
    val shipSpeed: Double = 1.0,
    val cargo: Double = 1.0
) {
    operator fun plus(other: PlanetBonus): PlanetBonus = PlanetBonus(
        miningRate = miningRate * other.miningRate,
        shipSpeed = shipSpeed * other.shipSpeed,
        cargo = cargo * other.cargo
    )
}

data class Bonus(
    val globalPlanetBonus: PlanetBonus = PlanetBonus(),
    val smeltSpeed: Double = 1.0,
    val craftSpeed: Double = 1.0,
    val managers: Double = 1.0,
    val projectCostReduction: Double = 0.0,
    val planetUpgradesReduction: Double = 0.0,
    val planetUpgradesReductionPerColonyLevel: Double = 0.0
) {
    operator fun plus(other: Bonus): Bonus = Bonus(
        globalPlanetBonus = globalPlanetBonus + other.globalPlanetBonus,
        smeltSpeed = smeltSpeed * other.smeltSpeed,
        craftSpeed = craftSpeed * other.craftSpeed,
        managers = managers * other.managers,
        projectCostReduction = projectCostReduction + other.projectCostReduction,
        planetUpgradesReduction = planetUpgradesReduction + other.planetUpgradesReduction,
        planetUpgradesReductionPerColonyLevel = planetUpgradesReductionPerColonyLevel + other.planetUpgradesReductionPerColonyLevel
    )
}

object Ships {

    val DAUGHTERSHIP = Bonus(
        globalPlanetBonus = PlanetBonus(
            miningRate = 1.5,
            shipSpeed = 1.25,
            cargo = 1.25
        )
    )

    val ELDERSHIP = Bonus(
        globalPlanetBonus = PlanetBonus(
            miningRate = 2.0,
            shipSpeed = 1.5,
            cargo = 1.5
        ),
        smeltSpeed = 1.5,
        craftSpeed = 1.5
    )

    val AURORA = Bonus(
        managers = 2.0
    )
}

enum class Room(
    val bonus: (Int) -> Bonus
) {
    ENGINEERING({ l -> Bonus(globalPlanetBonus = PlanetBonus(miningRate = 1.25 + (l-1) * 0.15)) }),
    FORGE({ l -> Bonus(smeltSpeed = 1.1 + (l-1) * 0.1) }),
    AERONAUTICAL({ l -> Bonus(globalPlanetBonus = PlanetBonus(shipSpeed = 1.5 + (l-1) * 0.25)) }),
}
