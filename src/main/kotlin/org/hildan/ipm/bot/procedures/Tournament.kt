@file:OptIn(ExperimentalTime::class)

package org.hildan.ipm.bot.procedures

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.adb.*
import org.hildan.ipm.bot.ui.*
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

internal suspend fun Adb.run6minArkLoop() {
    while (true) {
        val gotBonus = checkAndBuyArkBonus()
        delay(if (gotBonus) 6.minutes else 10.seconds)
    }
}

internal suspend fun Adb.runTournamentBackground() {
    var lastArkBonus = Instant.MIN
    while (true) {
        if (lastArkBonus < Instant.now().minusSeconds(6 * 60)) {
            val bonusReceived = checkAndBuyArkBonus()
            if (bonusReceived) {
                lastArkBonus = Instant.now()
            }
        }
        // we don't care if buttons are disabled here
        tap { planetButtons.mine }
        tap { planetButtons.ship }
        tap { planetButtons.cargo }
        tap { planetButtons.next }
    }
}

internal suspend fun Adb.runColonyLoop() {
    while (true) {
        when (buttonState(Buttons.colonizeDialog.colonize)) {
            ButtonState.ENABLED -> {
                tap { coloniesDialog.colonizeButton }
                tapWhenEnabled(Buttons.colonizeDialog.upgradeMine)
            }
            ButtonState.DISABLED, ButtonState.INVISIBLE -> {
                tap { coloniesDialog.nextPlanet }
            }
        }
        delay(20)
    }
}
