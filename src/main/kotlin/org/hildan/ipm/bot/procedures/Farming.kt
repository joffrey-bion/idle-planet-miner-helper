package org.hildan.ipm.bot.procedures

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.api.*
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.resources.OreType
import kotlin.random.Random
import kotlin.time.measureTimedValue
import kotlin.time.seconds

internal suspend fun ScreenWithGalaxyTopVisible.runCreditsFarmingLoop(): Nothing {
    infiniteLoop {
        checkAndBuyArkBonus()
        println(">>> Cycle start")
        val (screen, cycleDuration) = measureTimedValue {
            goToMothership().sellGalaxy(saveScreenshot = true).reach10M().also {
                val randomWait = Random.nextInt(until = 20).seconds
                println("Waiting for better galaxy value before next sell ($randomWait)")
                delay(randomWait) // let GV go over a couple more credits thresholds
            }
        }
        println(">>> Full cycle duration: $cycleDuration\n")
        screen
    }
}

private suspend fun ScreenWithGalaxyTopVisible.reach10M() = //
    also { println("Bootstrapping copper auto-sell") }
        .tapBalor() //
        .goToResources()
        .startAutoSell(OreType.COPPER)
        .also { println("Upgrading Balor") }
        .tapBalor()
        .upgradePlanetMSCM()
        .also { delay(500) } // sometimes we miss drasta click due to lag (or money?)
        .also { println("Upgrading Drasta") }
        .tapDrasta()
        .upgradePlanetMSCM()
        .goToResources()
        .startAutoSell(OreType.IRON)
        .tapBalor()
        .upgradeMine()
        .closePlanet()
        .also { println("Upgrading Anadius") }
        .tapAnadius()
        .upgradePlanetMSCM()
        .closePlanet()
        .also { println("Upgrading Dholen") }
        .tapDholen()
        .upgradePlanetMSCM()
        .nextPlanet()
        .nextPlanet()
        .upgradeMine()
        .also { println("Preparing auto-sell for project/prod setup") }
        .setupAutoSellForProjectsProgress()
        .also { println("Unlocking smelter project") }
        .unlockSmelterProject()
        .also { println("Setting up smelters (copper/iron)") }
        .setupCopperAndIronProduction()
        .also { println("Unlocking management project") }
        .goToProjects()
        .researchProject(Project.MANAGEMENT)
        .also { println("Assigning managers") }
        .assignManagers()
        .also { println("Unlocking telescope") }
        .goToProjects()
        .researchProject(Project.TELESCOPE_1)
        .also { println("Going full iron") }
        .goToProduction()
        .tapSmelter1()
        .selectRecipe2()
        .also { println("Preparing planet upgrade round 2") }
        .goToResources()
        .startAutoSell(OreType.COPPER) // we don't auto-sell iron to let prod at full speed
        .also { println("Round #2 of planet upgrades") }
        .planetMCUpgradeRound()
        .also { println("Unlocking beacon") }
        .goToProjects()
        .researchProject(Project.BEACON)
        .also { println("Preparing planet upgrade round 3") }
        .goToResources()
        .startAutoSell(OreType.IRON)
        .also { println("Round #3 of planet upgrades") }
        .tapDrasta()
        .upgradeMine()
        .nextPlanet()
        .upgradeMine()
        .also { println("Unlocking crafter") }
        .goToResources()
        .stopAutoSell(OreType.LEAD)
        .stopAutoSell(OreType.IRON) // let prod at full speed
        .closeResources()
        .goToProjects()
        .researchProject(Project.CRAFTER)
        .also { println("Setting up crafters") }
        .setupCraftersWithIronNails()
        .also { println("Final tweaks") }
        .goToResources()
        .startAutoSell(OreType.LEAD)
            // TODO auto-sell iron nails here
        .closeResources()
        .tapDholen()
        .upgradeMine()

private suspend fun BaseScreen.setupAutoSellForProjectsProgress() = //
    goToResources()
        .startAutoSell(OreType.LEAD)
        .stopAutoSell(OreType.IRON)
        .stopAutoSell(OreType.COPPER)

private suspend fun BaseScreen.unlockSmelterProject() = //
    goToProjects()
        .researchProject(Project.ASTEROID_MINER)
        .researchProject(Project.SMELTER)

private suspend fun BaseScreen.setupCopperAndIronProduction() = //
    goToProduction()
        .tapSmelter1()
        .selectRecipe1() // select copper bars
        .buySmelter2()
        .tapSmelter2()
        .buyRecipe2()
        .selectRecipe2() // select iron bars

private suspend fun BaseScreen.assignManagers() = //
    goToManagers()
        .assignFirstManager()
        .nextManagedPlanet()
        .assignFirstManager()
        .nextManagedPlanet()
        .assignFirstManager()
        .nextManagedPlanet()
        .assignFirstManager()

private suspend fun ScreenWithGalaxyTopVisible.planetMCUpgradeRound() = //
    tapBalor()
        .upgradeMine()
        .upgradeCargo()
        .nextPlanet() // Drasta (mine is never affordable at that point)
        .upgradeCargo()
        .nextPlanet() // Anadius
        .upgradeMine()
        .upgradeCargo()
        .nextPlanet() // Dholen
        .upgradeMine()
        .upgradeCargo()

private suspend fun BaseScreen.setupCraftersWithIronNails() = //
    goToProduction()
        .switchToCrafting()
        .tapCrafter1()
        .buyRecipe2()
        .selectRecipe2()
        .buyCrafter2()
        .tapCrafter2()
        .selectRecipe2()
