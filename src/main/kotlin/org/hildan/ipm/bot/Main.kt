@file:OptIn(ExperimentalTime::class)
package org.hildan.ipm.bot

import com.malinskiy.adam.interactor.StartAdbInteractor
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.minutes
import kotlin.time.seconds

fun main() = runBlocking {
    StartAdbInteractor().execute()

    val adb = connectFirstAdbDevice(OnePlus5Coords)

    adb.run6minArkLoop()
//    adb.runTournamentBackground()
//    adb.runCreditsFarmingLoop()
}

private suspend fun Adb.run6minArkLoop() {
    while (true) {
        val gotBonus = checkArkBonus()
        delay(if (gotBonus) 6.minutes else 10.seconds)
    }
}

private suspend fun Adb.runTournamentBackground() {
    while (true) {
        checkArkBonus()
        // we don't care if buttons are diabled here
        tap { planetButtons.mine }
        tap { planetButtons.ship }
        tap { planetButtons.cargo }
        tap { planetButtons.next }
    }
}

private suspend fun Adb.runCreditsFarmingLoop() {
    while (true) {
        checkArkBonus()
        val cycleDuration = measureTime {
            sellGalaxy()
            reach10M()
        }
        println(">>> Full cycle duration: $cycleDuration")
    }
}

private suspend fun Adb.sellGalaxy() {
    tap { navbar.mothership }
    tap { mothership.sellButton }
    tap { mothership.sellDialog.sellButton }
    tap { mothership.sellDialog.confirmSellButton }
}

private suspend fun Adb.reach10M() {
    println("Unlocking first planets")
    tap { planets.balor }
    tap { navbar.ore }
    longTap { ores.copper }
    tap { planets.balor }
    upgradePlanetMSCM()

    tap { planets.drasta }
    upgradePlanetMSCM()
    tap { navbar.ore }
    longTap { ores.iron }

    tap { planets.balor }
    waitAndTapPlanetButton { mine }
    tap { planetButtons.close }

    tap { planets.anadius }
    upgradePlanetMSCM()
    tap { planetButtons.close }

    tap { planets.dholen }
    upgradePlanetMSCM()
    tap { planetButtons.next }
    tap { planetButtons.next }
    waitAndTapPlanetButton { mine }

    println("Preparing auto-sell for project/prod setup")
    tap { navbar.ore }
    longTap { ores.lead }
    longTap { ores.iron } // stop auto-sell
    longTap { ores.copper } // stop auto-sell

    println("Unlocking smelter project")
    tap { navbar.projects }
    researchProject { projects.asteroidMiner }
    researchProject { projects.smelter }

    println("Setting up smelters")
    tap { navbar.production }
    tap { production.selectRecipe1Button }
    tap { production.recipePicker.recipe1 } // select copper bars
    tap { production.selectRecipe2Button } // unlock the slot
    tap { production.selectRecipe2Button } // open dialog
    tap { production.recipePicker.recipe2 } // unlock iron bars
    tap { production.recipePicker.recipe2 } // select iron bars

    println("Unlocking management project")
    tap { navbar.projects }
    researchProject { projects.management }

    println("Setting up managers")
    tap { navbar.managers }
    repeat(4) {
        tap { managers.firstManager }
        tap { managers.next }
    }

    println("Unlocking telescope")
    tap { navbar.projects }
    researchProject { projects.telescope1 }

    println("Going full iron")
    tap { navbar.production }
    tap { production.selectRecipe1Button }
    tap { production.recipePicker.recipe2 } // select iron bars

    tap { navbar.ore }
    longTap { ores.copper } // auto-sell
    // we don't auto-sell iron to let the production at full speed for beacon

    println("Round #2 of planet upgrades")
    tap { planets.balor }
    waitAndTapPlanetButton { mine }
    waitAndTapPlanetButton { cargo }
    tap { planetButtons.next }

    waitAndTapPlanetButton { cargo }
    tap { planetButtons.next }

    waitAndTapPlanetButton { mine }
    waitAndTapPlanetButton { cargo }
    tap { planetButtons.next }

    waitAndTapPlanetButton { mine }
    waitAndTapPlanetButton { cargo }

    println("Unlocking beacon")
    tap { navbar.projects }
    researchProject { projects.beacon }

    tap { navbar.ore }
    longTap { ores.iron } // auto-sell

    println("Round #3 of planet upgrades")
    tap { planets.drasta }
    waitAndTapPlanetButton { mine }
    tap { planetButtons.next }

    waitAndTapPlanetButton { mine }

    println("Unlocking crafter")
    tap { navbar.ore }
    longTap { ores.lead } // stop auto-sell
    longTap { ores.iron } // stop auto-sell to let production at full speed
    tap { ores.close }

    tap { navbar.projects }
    researchProject { projects.crafter }

    println("Settings up crafters")
    tap { navbar.production }
    tap { production.craftButton }
    tap { production.selectRecipe1Button }
    tap { production.recipePicker.recipe2 } // unlock iron nails
    tap { production.recipePicker.recipe2 } // select iron nails
    tap { production.selectRecipe2Button } // unlock the slot
    tap { production.selectRecipe2Button } // open dialog
    tap { production.recipePicker.recipe2 } // select iron bars

    tap { navbar.ore }
    longTap { ores.lead } // auto-sell
    // TODO activate auto-sell for iron nails? (we could keep upgrading planets this way)
    tap { ores.close }

    tap { planets.dholen }
    waitAndTapPlanetButton { mine }
}

private suspend fun Adb.upgradePlanetMSCM() {
    waitAndTapPlanetButton { mine }
    waitAndTapPlanetButton { ship }
    waitAndTapPlanetButton { cargo }
    waitAndTapPlanetButton { mine }
}

private suspend fun Adb.waitAndTapPlanetButton(buttonCoords: PlanetButtonsCoords.() -> Coords) {
    val nullIfTimedOut = withTimeoutOrNull(5_000) {
        waitAndTapButton(Colors.PLANET_BUTTON_ACTIVE) { planetButtons.buttonCoords() }
    }
    if (nullIfTimedOut == null) {
        System.err.println("Planet upgrade skipped because the button was still not available after 5s")
    }
}

private suspend fun Adb.researchProject(projectCoords: IpmCoords.() -> Coords) {
    tap(projectCoords)
    waitAndTapButton(Colors.RESEARCH_BUTTON_ACTIVE) { projects.researchDialogConfirm }
}

private suspend fun Adb.waitAndTapButton(activeColor: Color, buttonCoords: IpmCoords.() -> Coords) {
    awaitPixelColor(activeColor, coords = buttonCoords)
    tap(buttonCoords)
}

private suspend fun Adb.checkArkBonus(): Boolean {
    print("Checking Ark bonus...")
    val color = pixelColor { arkBonusButton }
    val bonusPresent = color == Colors.ARK_BONUS_ICON
    if (bonusPresent) {
        println("Available!")
        tap { arkBonusButton }
        tap { arkClaim }
    } else {
        println("Nope.")
    }
    return bonusPresent
}
