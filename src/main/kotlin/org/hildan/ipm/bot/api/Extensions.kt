package org.hildan.ipm.bot.api

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.adb.clippedTo
import org.hildan.ipm.bot.ocr.ocr
import org.hildan.ipm.bot.ui.Clips
import org.hildan.ipm.bot.ui.Ocrs
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.planets.Planet
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Paths

private val ipmDataDir = Paths.get("${System.getProperty("user.home")}/.ipm").apply {
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
        claimArkBonus()
    } else {
        println("Nope.")
    }
    return bonusPresent
}

internal suspend fun MothershipScreen.sellGalaxy(saveGalaxyValue: Boolean = false): MothershipScreen {
    return tapSellGalaxy()
        .apply {
            if (saveGalaxyValue) {
                delay(500) // wait for dialog
                saveGalaxyValue()
            }
        }
        .tapRegularSell()
        .tapConfirmGalaxySell()
}

private suspend fun SellGalaxyDialog.saveGalaxyValue() {
    val galaxyValue = readGalaxyValue()

    val secondsSinceLastSave = secondsSinceLastSave() ?: return // we don't want to record if we don't know the duration
    val galaxyValuesCsv = ipmDataDir.resolve("galaxy-values.csv").toFile()
    if (!galaxyValuesCsv.exists()) {
        galaxyValuesCsv.appendText("Seconds, Galaxy value\n")
    }
    galaxyValuesCsv.appendText("$secondsSinceLastSave, $galaxyValue\n")
}

private suspend fun SellGalaxyDialog.readGalaxyValue(): BigInteger = adb.screenshot() //
    .clippedTo(Clips.galaxyValueInSellDialog) //
    .ocr(Ocrs.galaxyValueInSellDialog) //
    .parseDollarAmount()

private var lastSave: Long? = null

private fun secondsSinceLastSave(): Double? {
    val now = System.currentTimeMillis()
    val last = lastSave
    lastSave = now
    return if (last == null) null else (now - last) / 1000.0
}

private fun String.parseDollarAmount(): BigInteger {
    require(isNotBlank()) { "Parsed amount is blank, maybe not on the right screen?" }
    require(first() == '$') { "Expected dollar as first character" }
    val amountText = drop(1).dropLast(1).replace('B', '8') // sometimes 8 yields B
    val unit = last()
    val power10 = unit.unitPower10()
    val amount = amountText.toBigDecimalOrNull() ?: error("Bad OCR result, cannot convert '${this}' to a cash amount")
    return (amount * BigDecimal.TEN.pow(power10)).toBigInteger()
}

private fun Char.unitPower10(): Int = when (this) {
    'K' -> 3
    'M' -> 6
    'B' -> 9
    'T' -> 12
    'q' -> 15
    'Q' -> 18
    else -> error("Unsupported unit '${this}'")
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
