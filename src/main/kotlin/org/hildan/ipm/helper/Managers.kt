package org.hildan.ipm.helper

data class Manager(
    val name: String,
    val planetBonus: PlanetBonus,
    val otherBonus: Bonus? = null
)
