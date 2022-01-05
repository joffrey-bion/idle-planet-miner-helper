package org.hildan.ipm.bot

import kotlinx.coroutines.runBlocking
import org.hildan.ipm.bot.adb.Adb
import org.hildan.ipm.bot.api.AllScreensImpl
import org.hildan.ipm.bot.api.GalaxyScreen
import org.hildan.ipm.bot.procedures.runCreditsFarmingLoop
import org.hildan.ipm.bot.ui.OnePlus5CoordsMap

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
