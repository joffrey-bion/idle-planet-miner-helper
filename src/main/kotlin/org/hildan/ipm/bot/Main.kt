@file:OptIn(ExperimentalTime::class)
package org.hildan.ipm.bot

import kotlinx.coroutines.runBlocking
import org.hildan.ipm.bot.adb.*
import org.hildan.ipm.bot.api.*
import org.hildan.ipm.bot.procedures.*
import org.hildan.ipm.bot.ui.OnePlus5CoordsMap
import kotlin.time.ExperimentalTime

fun main(): Unit = runBlocking {
    val adb = Adb.connectToFirstDevice(coordsMap = OnePlus5CoordsMap)
    val screen = AllScreensImpl(adb) as GalaxyScreen // change this to match whatever screen is currently running

//    println(adb.pixelColor { arkClaim })
//    screen.runColonyLoop()
//    screen.run6minArkLoop()
//    screen.runTournamentBackground()
//    screen.clearManagers()
//    screen.assignManagers()
    screen.runCreditsFarmingLoop()
}

// FIXME detect daily task and do not confuse with Ark bonus
// TODO detect rover, claim and relaunch (don't forget to ensure rover project is researched!)

// TODO fully type-safe state
// - navbar is always visible (even in managers screen, even with dialogs)
// - all navbar panels are the same height, except managers that are full screen
// - when a non-manager panel is open, it leaves all relevant top-right icons visible (even with ark bonus active)
// - ark bonus moves the rover down
// - planet panel (with colony button) leaves 4 icons in the top-right corner (rover never visible, but the ark is)

// top-right icons:
// - settings
// - achievements
// - permanent mining bonus
// - daily gifts (when available, once a day) - FIXME detected as ark bonus!!
// - ark bonus (when available)
// - daily tasks
// - rover (even when it's not unlocked)
// - tournament (when available)
// - challenge (when available) - TODO check if before tournament
