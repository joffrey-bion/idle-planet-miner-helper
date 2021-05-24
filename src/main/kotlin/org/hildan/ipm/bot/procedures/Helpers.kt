package org.hildan.ipm.bot.procedures

import org.hildan.ipm.bot.adb.*
import org.hildan.ipm.bot.ui.*
import org.hildan.ipm.helper.galaxy.planets.Planet
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

internal suspend fun Adb.upgradePlanetMSCM() {
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    waitAndTapPlanetButton(Buttons.planet.upgradeShip)
    waitAndTapPlanetButton(Buttons.planet.upgradeCargo)
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
}

@OptIn(ExperimentalTime::class)
internal suspend fun Adb.waitAndTapPlanetButton(button: Button) {
    tapWhenEnabled(button, timeout = 5.seconds)
}

internal suspend fun Adb.researchProject(projectCoords: CoordsMap.() -> Coords) {
    tap(projectCoords)
    tapWhenEnabled(Buttons.projects.research)
}

internal suspend fun Adb.checkAndBuyArkBonus(): Boolean {
    print("Checking Ark bonus... ")
    val bonusPresent = isArkBonusPresent()
    if (bonusPresent) {
        println("Available! Let's buy.")
        tap { arkBonusIcon }
        tapWhenEnabled(Buttons.arkClaim)
    } else {
        println("Nope.")
    }
    return bonusPresent
}

private suspend fun Adb.isArkBonusPresent(): Boolean = pixelColor { arkBonusIcon } == Colors.arkBonusIcon

internal suspend fun Adb.clearManagers() {
    repeat(Planet.values().size) {
        tap { managers.assignedManager }
        tap { managers.nextPlanet }
    }
}

/**
 * Assumes starting from last planet, with "Hide addigned managers" enabled.
 */
internal suspend fun Adb.assignManagers() {
    repeat(Planet.values().size) {
        tap { managers.firstManager }
        tap { managers.prevPlanet }
    }
}
