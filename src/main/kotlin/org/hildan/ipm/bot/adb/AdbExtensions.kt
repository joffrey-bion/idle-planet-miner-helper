package org.hildan.ipm.bot.adb

import kotlinx.coroutines.withTimeoutOrNull
import org.hildan.ipm.bot.ui.Button
import org.hildan.ipm.bot.ui.Coords
import org.hildan.ipm.bot.ui.PlatonicCoords
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

suspend fun Adb.tap(coords: PlatonicCoords) = tap(coords.resolve())

suspend fun Adb.tap(coords: Coords) {
    shell("input tap ${coords.x} ${coords.y}")
}

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: PlatonicCoords) =
    longTap(duration, coords.resolve())

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: Coords) {
    shell("input swipe ${coords.x} ${coords.y} ${coords.x} ${coords.y} ${duration.inMilliseconds.toInt()}")
}

/**
 * Waits until the given [button] is enabled and then taps it.
 * The color is checked every [retryDelay].
 * If the button doesn't become enabled before [timeout], this method gives up and returns false.
 */
internal suspend fun Adb.tapWhenEnabled(
    button: Button,
    retryDelay: Duration = 200.milliseconds,
    timeout: Duration = 1.minutes,
    timeoutMsg: String = "WARN: tap was skipped because the button stayed disabled for more than $timeout"
) {
    withTimeoutOrNull(timeout) {
        val time = measureTime {
            awaitPixelColor(button.colors.enabled, pollingPeriod = retryDelay, coords = button.colorLocation)
        }
        if (time > 2.seconds) {
            System.err.println("WARN: awaited $time for button (color ${button.colors.enabled} at ${button.colorLocation.resolve()})")
        }
        tap(button.tapLocation)
    } ?: run {
        System.err.println(timeoutMsg)
    }
}
