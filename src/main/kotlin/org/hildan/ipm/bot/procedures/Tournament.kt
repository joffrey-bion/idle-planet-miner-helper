@file:OptIn(ExperimentalTime::class)

package org.hildan.ipm.bot.procedures

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.api.ColonyDialog
import org.hildan.ipm.bot.api.PlanetScreen
import org.hildan.ipm.bot.api.ScreenWithArkBonusVisible
import org.hildan.ipm.bot.api.checkAndBuyArkBonus
import org.hildan.ipm.bot.api.infiniteLoop
import org.hildan.ipm.bot.ui.*
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

internal suspend fun ScreenWithArkBonusVisible.run6minArkLoop(): Nothing {
    while (true) {
        val gotBonus = checkAndBuyArkBonus()
        delay(if (gotBonus) 6.minutes else 10.seconds)
    }
}

internal suspend fun PlanetScreen.runTournamentBackground(): Nothing {
    var lastArkBonus = Instant.MIN
    infiniteLoop {
        if (lastArkBonus < Instant.now().minusSeconds(6 * 60)) {
            val bonusReceived = checkAndBuyArkBonus()
            if (bonusReceived) {
                lastArkBonus = Instant.now()
            }
        }
        // we don't care if buttons are disabled here
        tapMine()
        tapShip()
        tapCargo()
        nextPlanet()
    }
}

internal suspend fun ColonyDialog.runColonyLoop(): Nothing {
    infiniteLoop {
        when (readColonyButtonState()) {
            ButtonState.ENABLED -> tapColonize().pickMineColonizationBonus()
            ButtonState.DISABLED, ButtonState.INVISIBLE -> nextColonizedPlanet()
        }
    }
}
