package org.hildan.ipm.bot.api

import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.planets.Planet

internal suspend fun PlanetScreen.upgradePlanetMSCM() = upgradeMine().upgradeShip().upgradeCargo().upgradeMine()

internal suspend fun ProjectsScreen.researchProject(project: Project) = tapProject(project).confirmResearch()

internal suspend inline fun <reified T : ScreenWithArkBonusVisible> T.claimArkBonus(): T =
    tapArkBonus().claimArkBonus() as T

internal suspend fun ScreenWithArkBonusVisible.checkAndBuyArkBonus(): Boolean {
    print("Checking Ark bonus... ")
    val bonusPresent = isArkBonusPresent()
    if (bonusPresent) {
        println("Available! Let's buy.")
        tapArkBonus().claimArkBonus()
    } else {
        println("Nope.")
    }
    return bonusPresent
}

internal suspend fun MothershipScreen.sellGalaxy() = tapSellGalaxy().tapRegularSell().tapConfirmGalaxySell()

internal suspend fun ManagersScreen.clearManagers() {
    repeat(Planet.values().size) {
        removeAssignedManager()
        nextManagedPlanet()
    }
}

/**
 * Assumes starting from last planet, with "Hide assigned managers" enabled.
 */
internal suspend fun ManagersScreen.assignManagers() {
    repeat(Planet.values().size) {
        assignFirstManager()
        prevManagedPlanet()
    }
}
