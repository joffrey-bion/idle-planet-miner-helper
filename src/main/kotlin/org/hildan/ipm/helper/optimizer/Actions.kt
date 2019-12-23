package org.hildan.ipm.helper.optimizer

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.planets.PlanetType
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.min
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.utils.INFINITE_TIME
import org.hildan.ipm.helper.utils.next
import java.time.Duration

data class AppliedAction(
    val action: Action,
    val newGalaxy: Galaxy,
    val requiredCash: Price,
    val requiredResources: Resources,
    val time: Duration,
    val incomeRateGain: ValueRate
) {
    override fun toString(): String = """
        $action
            Cost:        $requiredCash and $requiredResources
            Time:        $time
            Income gain: ${if (incomeRateGain > ValueRate.ZERO) incomeRateGain else "none"}
    """.trimIndent()
}

fun Galaxy.possibleActions(): List<AppliedAction> {
    val buyPlanetActions = unlockedPlanets
        .filter { totalIncomeRate > ValueRate.ZERO || it.unlockPrice <= currentCash }
        .map { Action.BuyPlanet(it).performOn(this) }
    val upgradeActions = planets.flatMap {
        listOf(
            Action.Upgrade.Mine(it.type, it.mineLevel + 1).performOn(this),
            Action.Upgrade.Ship(it.type, it.shipLevel + 1).performOn(this),
            Action.Upgrade.Cargo(it.type, it.cargoLevel + 1).performOn(this)
        )
    }
    val researchActions = researchProjectActions()
    val unlockRecipeActions = unlockRecipeActions()
    return buyPlanetActions + upgradeActions + researchActions + unlockRecipeActions
}

private fun Galaxy.researchProjectActions(): List<AppliedAction> =
        // not checking actual resources (after bonus) because resource types are the same
        bonuses.unlockedProjects
            .filter { it.requiredResources.areAccessible() }
            .map { Action.Research(it).performOn(this) }

private fun Galaxy.unlockRecipeActions(): List<AppliedAction> {
    val unlockRecipeActions = mutableListOf<AppliedAction>()
    val nextAlloyRecipe = highestUnlockedAlloyRecipe.next()
    val nextItemRecipe = highestUnlockedItemRecipe.next()
    if (nbSmelters > 0 && nextAlloyRecipe != null) {
        unlockRecipeActions.add(Action.UnlockSmeltRecipe(nextAlloyRecipe).performOn(this))
    }
    if (nbCrafters > 0 && nextItemRecipe != null) {
        unlockRecipeActions.add(Action.UnlockCraftRecipe(nextItemRecipe).performOn(this))
    }
    return unlockRecipeActions
}

private fun Galaxy.createAction(
    action: Action,
    newGalaxy: Galaxy,
    requiredCash: Price = Price.ZERO,
    requiredResources: Resources = Resources.NOTHING
): AppliedAction {
    val cashInstantlySpent = min(currentCash, requiredCash)
    val remainingToPay = requiredCash - cashInstantlySpent
    val timeWaitingForCash = when {
        remainingToPay == Price.ZERO -> Duration.ZERO
        totalIncomeRate == ValueRate.ZERO -> error("action costing extra money in 0-income galaxy")
        else -> remainingToPay / totalIncomeRate
    }
    val timeWaitingForResources = getApproximateTime(requiredResources)
    return AppliedAction(
        action = action,
        newGalaxy = newGalaxy.copy(currentCash = currentCash - cashInstantlySpent),
        requiredCash = requiredCash,
        requiredResources = requiredResources,
        time = max(timeWaitingForCash, timeWaitingForResources),
        incomeRateGain = newGalaxy.totalIncomeRate - totalIncomeRate
    )
}

private fun max(d1: Duration, d2: Duration) = if (d1 < d2) d2 else d1

sealed class Action {

    abstract fun performOn(galaxy: Galaxy): AppliedAction

    data class BuyPlanet(val planet: PlanetType) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withBoughtPlanet(planet),
            requiredCash = planet.unlockPrice
        )

        override fun toString(): String = "Buy planet $planet"
    }

    sealed class Upgrade(
        open val planet: PlanetType,
        open val targetLevel: Int
    ) : Action() {
        fun toString(upgradedElementName: String): String =
                "Upgrade $planet's $upgradedElementName to level $targetLevel"

        data class Mine(override val planet: PlanetType, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withMineLevel(planet, targetLevel),
                requiredCash = galaxy.planetCosts[planet]!!.mineUpgrade
            )

            override fun toString(): String = super.toString("MINE")
        }

        data class Ship(override val planet: PlanetType, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withShipLevel(planet, targetLevel),
                requiredCash = galaxy.planetCosts[planet]!!.shipUpgrade
            )

            override fun toString(): String = super.toString("SHIP")
        }

        data class Cargo(override val planet: PlanetType, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withCargoLevel(planet, targetLevel),
                requiredCash = galaxy.planetCosts[planet]!!.cargoUpgrade
            )

            override fun toString(): String = super.toString("CARGO")
        }
    }

    data class Research(val project: Project): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withProject(project),
            requiredResources = galaxy.bonuses.constant.actualResourcesRequiredByProject.getValue(project)
        )

        override fun toString(): String = "Research project $project"
    }

    data class UnlockSmeltRecipe(val alloy: AlloyType): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.copy(highestUnlockedAlloyRecipe = alloy),
            requiredCash = alloy.recipeUnlockPrice
        )

        override fun toString(): String = "Unlock smelter recipe $alloy"
    }

    data class UnlockCraftRecipe(val item: ItemType): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.copy(highestUnlockedItemRecipe = item),
            requiredCash = item.recipeUnlockPrice
        )

        override fun toString(): String = "Unlock crafter recipe $item"
    }

    data class SwitchSmeltRecipe(val alloy: AlloyType): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy
        )

        override fun toString(): String = "Switch all smelters to $alloy"
    }

    data class SwitchCraftRecipe(val item: ItemType): Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy
        )

        override fun toString(): String = "Switch all crafters to $item"
    }
}
