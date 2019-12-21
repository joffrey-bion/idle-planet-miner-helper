package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.resources.ResourceType

data class ChallengeStars(
    private val stars: Map<ResourceType, Int> = emptyMap()
) {
    val bonus =
            Bonus(values = ResourceValuesBonus(
                resourceMultipliers = stars.mapValues {
                    Multiplier(1 + 0.2 * it.value)
                })
            )
}
