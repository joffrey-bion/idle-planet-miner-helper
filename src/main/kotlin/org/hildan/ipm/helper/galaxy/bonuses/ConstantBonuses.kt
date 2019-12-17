package org.hildan.ipm.helper.galaxy.bonuses

data class ConstantBonuses(
    private val shipsBonus: Bonus,
    private val roomsBonus: Bonus,
    private val beaconBonus: Bonus,
    private val managerAssignment: ManagerAssignment = ManagerAssignment(),
    private val market: Market,
    private val stars: ChallengeStars
) {
    private val withoutBeacon =
            shipsBonus + roomsBonus + managerAssignment.totalBonus + market.totalBonus + stars.totalBonus

    private val withBeacon = withoutBeacon + beaconBonus

    fun total(beaconActive: Boolean) = if (beaconActive) withBeacon else withoutBeacon
}
