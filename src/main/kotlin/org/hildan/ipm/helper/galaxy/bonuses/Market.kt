package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.resources.ResourceType

data class Market(
    private val multipliers: Map<ResourceType, Multiplier> = emptyMap()
) {
    val bonus = Bonus(
        values = ResourceValuesBonus(
            resourceMultipliers = multipliers
        )
    )
}
