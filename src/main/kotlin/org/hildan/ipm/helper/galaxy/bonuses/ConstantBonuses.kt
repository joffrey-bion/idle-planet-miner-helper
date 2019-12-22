package org.hildan.ipm.helper.galaxy.bonuses

import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.completeEnumMap

data class ConstantBonuses(
    private val shipsBonus: Bonus,
    private val roomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val managerAssignment: ManagerAssignment = ManagerAssignment(),
    private val market: Market,
    private val stars: ChallengeStars
) {
    private val withoutBeacon = shipsBonus + roomsBonus + managerAssignment.bonus + market.bonus + stars.bonus

    private val withBeacon = withoutBeacon + beaconBonus

    val actualResourcesRequiredByProject: Map<Project, Resources> = completeEnumMap {
        withoutBeacon.projectCostMultiplier.applyTo(it.requiredResources)
    }

    fun total(beaconActive: Boolean) = if (beaconActive) withBeacon else withoutBeacon
}
