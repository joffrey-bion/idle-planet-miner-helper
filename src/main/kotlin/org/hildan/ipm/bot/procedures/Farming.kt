package org.hildan.ipm.bot.procedures

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.api.*
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.resources.OreType
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
internal suspend fun ScreenWithGalaxyTopVisible.runCreditsFarmingLoop() {
    var screen = this
    while (true) {
        checkAndBuyArkBonus()
        println(">>> Cycle start")
        val cycleDuration = measureTime {
            screen = goToMothership().sellGalaxy().reach10M()
        }
        println(">>> Full cycle duration: $cycleDuration")
    }
}

private suspend fun ScreenWithGalaxyTopVisible.reach10M() = //
    upgradeInitialPlanets()
        .also { println("Preparing auto-sell for project/prod setup") }
        .setupAutoSellForProjectsProgress()
        .also { println("Unlocking smelter project") }
        .unlockSmelterProject()
        .also { println("Setting up smelters (copper/iron)") }
        .setupCopperAndIronProduction()
        .also { println("Unlocking management project") }
        .goToProjects()
        .researchProject(Project.MANAGEMENT)
        .also { println("Setting up managers") }
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
        .planetUpgradeRound()
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
        .setupCrafters()
        .also { println("Final tweaks") }
        .goToResources()
        .startAutoSell(OreType.LEAD)
        .closeResources()
        .tapDholen()
        .upgradeMine()

private suspend fun ScreenWithGalaxyTopVisible.upgradeInitialPlanets() = //
    tapBalor()
        .goToResources()
        .startAutoSell(OreType.COPPER)
        .tapBalor()
        .upgradePlanetMSCM()
        .also { delay(100) } // sometimes we miss drasta click due to lag (or money?)
        .tapDrasta()
        .upgradePlanetMSCM()
        .goToResources()
        .startAutoSell(OreType.IRON)
        .tapBalor()
        .upgradeMine()
        .closePlanet()
        .tapAnadius()
        .upgradePlanetMSCM()
        .closePlanet()
        .tapDholen()
        .upgradePlanetMSCM()
        .nextPlanet()
        .nextPlanet()
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

private suspend fun ScreenWithGalaxyTopVisible.planetUpgradeRound() = //
    tapBalor()
        .upgradeMine()
        .upgradeCargo()
        .nextPlanet() // Drasta
        .upgradeCargo()
        .nextPlanet() // Anadius
        .upgradeMine()
        .upgradeCargo()
        .nextPlanet() // Dholen
        .upgradeMine()
        .upgradeCargo()

private suspend fun BaseScreen.setupCrafters() = //
    goToProduction()
        .switchToCrafting()
        .tapCrafter1()
        .buyRecipe2()
        .selectRecipe2()
        .buySmelter2()
        .tapCrafter2()
        .selectRecipe2()
