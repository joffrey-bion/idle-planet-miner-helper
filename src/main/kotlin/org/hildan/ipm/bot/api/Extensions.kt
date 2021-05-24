package org.hildan.ipm.bot.api

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.adb.saveScreenshot
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.planets.Planet
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val screenshotsDir = Paths.get("${System.getProperty("user.home")}/.ipm/screenshots").apply {
    toFile().mkdirs()
}

// this ensures the type safety of the loop body
internal inline fun <T : BaseScreen> T.infiniteLoop(body: T.() -> T): Nothing {
    while (true) {
        body()
    }
}

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

internal suspend fun MothershipScreen.sellGalaxy(saveScreenshot: Boolean = false): MothershipScreen {
    return tapSellGalaxy()
        .apply {
            if (saveScreenshot) {
                delay(500) // wait for dialog
                adb.saveScreenshot(generateScreenshotPath())
            }
        }
        .tapRegularSell()
        .tapConfirmGalaxySell()
}

private fun generateScreenshotPath(): Path {
    val formattedNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"))
    return Files.createTempFile(screenshotsDir, "screenshot-${formattedNow}_", ".png")
}

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
