package org.hildan.ipm.helper

fun Sequence<AppliedAction>.compact() = sequence<AppliedAction> {
    var current: AppliedAction? = null
    for (next in this@compact) {
        if (current == null) {
            current = next
        } else {
            val combined = current.combineWith(next)
            if (combined != null) {
                current = combined
            } else {
                yield(current)
                current = next
            }
        }
    }
    if (current != null) {
        yield(current)
    }
}

private fun AppliedAction.combineWith(next: AppliedAction): AppliedAction? = when {
    areCombinableUpgrades(action, next.action) -> combineWith(next) { _, a2 -> a2 }
    else -> null
}

private fun AppliedAction.combineWith(next: AppliedAction, mergeActions: (Action, Action) -> Action): AppliedAction =
        AppliedAction(
            action = mergeActions(action, next.action),
            newGalaxy = next.newGalaxy,
            requiredCash = requiredCash + next.requiredCash,
            requiredResources = requiredResources + next.requiredResources,
            time = time + next.time,
            incomeRateGain = incomeRateGain + next.incomeRateGain
        )

private fun areCombinableUpgrades(action: Action, next: Action): Boolean {
    val upgradeTheSamePlanet = action is Action.Upgrade && next is Action.Upgrade && action.planet == next.planet
    if (!upgradeTheSamePlanet) {
        return false
    }
    return when {
        action is Action.Upgrade.Mine && next is Action.Upgrade.Mine -> true
        action is Action.Upgrade.Ship && next is Action.Upgrade.Ship -> true
        action is Action.Upgrade.Cargo && next is Action.Upgrade.Cargo -> true
        else -> false
    }
}
