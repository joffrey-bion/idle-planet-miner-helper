package org.hildan.ipm.helper

data class Manager(
    val name: String,
    val planetBonus: PlanetBonus,
    val globalBonus: Bonus = Bonus.NONE
) {
    fun toBonus(associatedPlanet: PlanetType): Bonus =
            globalBonus + Bonus(perPlanet = mapOf(associatedPlanet to planetBonus).asEMap { PlanetBonus.NONE })
}
