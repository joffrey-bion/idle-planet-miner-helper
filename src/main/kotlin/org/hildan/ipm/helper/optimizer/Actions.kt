package org.hildan.ipm.helper.optimizer

import org.hildan.ipm.helper.galaxy.Galaxy
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.money.Price
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.money.ValueRate
import org.hildan.ipm.helper.galaxy.money.min
import org.hildan.ipm.helper.galaxy.resources.AlloyType
import org.hildan.ipm.helper.galaxy.resources.Crafters
import org.hildan.ipm.helper.galaxy.resources.ItemType
import org.hildan.ipm.helper.galaxy.resources.Resources
import org.hildan.ipm.helper.galaxy.resources.Smelters
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
    val upgradeActions = planets.states.flatMap {
        listOf(
            Action.Upgrade.Mine(it.planet, it.mineLevel + 1).performOn(this),
            Action.Upgrade.Ship(it.planet, it.shipLevel + 1).performOn(this),
            Action.Upgrade.Cargo(it.planet, it.cargoLevel + 1).performOn(this)
        )
    }
    val researchActions = researchProjectActions()
    val productionActions = productionActions()
    return buyPlanetActions + upgradeActions + researchActions + productionActions
}

private fun Galaxy.researchProjectActions(): List<AppliedAction> =
        // not checking actual resources (after bonus) because resource types are the same
        bonuses.unlockedProjects
            .filter { it.requiredResources.areAccessible() }
            .map { Action.Research(it).performOn(this) }

private fun Galaxy.productionActions(): List<AppliedAction> {
    val productionActions = mutableListOf<AppliedAction>()
    if (nbSmelters > 0) {
        if (nbSmelters < Smelters.MAX) {
            productionActions.add(Action.BuySmelter.performOn(this))
        }
        val nextAlloyRecipe = highestUnlockedAlloyRecipe.next()
        if (nextAlloyRecipe != null) {
            productionActions.add(Action.UnlockSmeltRecipe(nextAlloyRecipe).performOn(this))
        }
    }
    if (nbCrafters > 0) {
        if (nbCrafters < Crafters.MAX) {
            productionActions.add(Action.BuyCrafter.performOn(this))
        }
        val nextItemRecipe = highestUnlockedItemRecipe.next()
        if (nextItemRecipe != null) {
            productionActions.add(Action.UnlockCraftRecipe(nextItemRecipe).performOn(this))
        }
    }
    return productionActions
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

    data class BuyPlanet(val planet: Planet) : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.withBoughtPlanet(planet),
            requiredCash = planet.unlockPrice
        )

        override fun toString(): String = "Buy planet $planet"
    }

    sealed class Upgrade(
        open val planet: Planet,
        open val targetLevel: Int
    ) : Action() {
        fun toString(upgradedElementName: String): String =
                "Upgrade $planet's $upgradedElementName to level $targetLevel"

        data class Mine(override val planet: Planet, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withMineLevel(planet, targetLevel),
                requiredCash = galaxy.planets.upgradeCosts.getValue(planet).mineUpgrade
            )

            override fun toString(): String = super.toString("MINE")
        }

        data class Ship(override val planet: Planet, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withShipLevel(planet, targetLevel),
                requiredCash = galaxy.planets.upgradeCosts.getValue(planet).shipUpgrade
            )

            override fun toString(): String = super.toString("SHIP")
        }

        data class Cargo(override val planet: Planet, override val targetLevel: Int) : Upgrade(planet, targetLevel) {

            override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
                action = this,
                newGalaxy = galaxy.withCargoLevel(planet, targetLevel),
                requiredCash = galaxy.planets.upgradeCosts.getValue(planet).cargoUpgrade
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

    object BuySmelter : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.copy(nbSmelters = galaxy.nbSmelters + 1),
            requiredCash = Smelters.priceForOneMore(galaxy.nbSmelters)
        )

        override fun toString(): String = "Buy one more smelter"
    }

    object BuyCrafter : Action() {

        override fun performOn(galaxy: Galaxy): AppliedAction = galaxy.createAction(
            action = this,
            newGalaxy = galaxy.copy(nbCrafters = galaxy.nbCrafters + 1),
            requiredCash = Crafters.priceForOneMore(galaxy.nbCrafters)
        )

        override fun toString(): String = "Buy one more crafter"
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
