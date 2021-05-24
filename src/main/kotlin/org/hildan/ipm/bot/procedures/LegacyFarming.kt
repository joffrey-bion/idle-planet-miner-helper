package org.hildan.ipm.bot.procedures

import org.hildan.ipm.bot.adb.*
import org.hildan.ipm.bot.ui.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
internal suspend fun Adb.runCreditsFarmingLoop() {
    while (true) {
        checkAndBuyArkBonus()
        val cycleDuration = measureTime {
            sellGalaxy()
            reach10M()
        }
        println(">>> Full cycle duration: $cycleDuration")
    }
}

private suspend fun Adb.sellGalaxy() {
    tap { navbar.mothership }
    tap { mothership.sellGalaxyButton }
    tap { mothership.sellGalaxyDialog.regularSellButton }
    tap { mothership.confirmSellDialog.confirmButton }
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
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    tap { planetButtons.close }

    tap { planets.anadius }
    upgradePlanetMSCM()
    tap { planetButtons.close }

    tap { planets.dholen }
    upgradePlanetMSCM()
    tap { planetButtons.next }
    tap { planetButtons.next }
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)

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
    tap { production.slot1RecipeButton }
    tap { production.recipePicker.recipe1 } // select copper bars
    tap { production.slot2RecipeButton } // unlock the slot
    tap { production.slot2RecipeButton } // open dialog
    tap { production.recipePicker.recipe2 } // unlock iron bars
    tap { production.recipePicker.recipe2 } // select iron bars

    println("Unlocking management project")
    tap { navbar.projects }
    researchProject { projects.management }

    println("Setting up managers")
    tap { navbar.managers }
    repeat(4) {
        tap { managers.firstManager }
        tap { managers.nextPlanet }
    }

    println("Unlocking telescope")
    tap { navbar.projects }
    researchProject { projects.telescope1 }

    println("Going full iron")
    tap { navbar.production }
    tap { production.slot1RecipeButton }
    tap { production.recipePicker.recipe2 } // select iron bars

    tap { navbar.ore }
    longTap { ores.copper } // auto-sell
    // we don't auto-sell iron to let the production at full speed for beacon

    println("Round #2 of planet upgrades")
    tap { planets.balor }
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    waitAndTapPlanetButton(Buttons.planet.upgradeCargo)
    tap { planetButtons.next }

    waitAndTapPlanetButton(Buttons.planet.upgradeCargo)
    tap { planetButtons.next }

    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    waitAndTapPlanetButton(Buttons.planet.upgradeCargo)
    tap { planetButtons.next }

    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    waitAndTapPlanetButton(Buttons.planet.upgradeCargo)

    println("Unlocking beacon")
    tap { navbar.projects }
    researchProject { projects.beacon }

    tap { navbar.ore }
    longTap { ores.iron } // auto-sell

    println("Round #3 of planet upgrades")
    tap { planets.drasta }
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
    tap { planetButtons.next }

    waitAndTapPlanetButton(Buttons.planet.upgradeMine)

    println("Unlocking crafter")
    tap { navbar.ore }
    longTap { ores.lead } // stop auto-sell
    longTap { ores.iron } // stop auto-sell to let production at full speed
    tap { closeNavPanel }

    tap { navbar.projects }
    researchProject { projects.crafter }

    println("Settings up crafters")
    tap { navbar.production }
    tap { production.craftButton }
    tap { production.slot1RecipeButton }
    tap { production.recipePicker.recipe2 } // unlock iron nails
    tap { production.recipePicker.recipe2 } // select iron nails
    tap { production.slot2RecipeButton } // unlock the slot
    tap { production.slot2RecipeButton } // open dialog
    tap { production.recipePicker.recipe2 } // select iron bars

    tap { navbar.ore }
    longTap { ores.lead } // auto-sell
    // TODO activate auto-sell for iron nails? (we could keep upgrading planets this way)
    tap { closeNavPanel }

    tap { planets.dholen }
    waitAndTapPlanetButton(Buttons.planet.upgradeMine)
}
