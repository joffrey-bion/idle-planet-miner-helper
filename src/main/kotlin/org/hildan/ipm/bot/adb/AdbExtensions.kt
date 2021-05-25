package org.hildan.ipm.bot.adb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.hildan.ipm.bot.ui.Button
import org.hildan.ipm.bot.ui.ButtonState
import org.hildan.ipm.bot.ui.ButtonStateColors
import org.hildan.ipm.bot.ui.Color
import org.hildan.ipm.bot.ui.Coords
import org.hildan.ipm.bot.ui.CoordsMap
import org.hildan.ipm.bot.ui.PlatonicCoords
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
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
        awaitPixelColor(button.colors.enabled, retryDelay = retryDelay, coords = button.colorLocation)
        tap(button.tapLocation)
    } ?: run {
        System.err.println(timeoutMsg)
    }
}

private suspend fun Adb.awaitPixelColor(
    targetColor: Color,
    retryDelay: Duration = 200.milliseconds,
    coords: PlatonicCoords,
) {
    val resolvedCoords = coords.resolve()
    val totalAwaited = measureTime {
        var color = pixelColor(resolvedCoords)
        while (color != targetColor) {
            delay(retryDelay)
            color = pixelColor(resolvedCoords)
        }
    }
    if (totalAwaited > 2.seconds) {
        System.err.println("WARN: awaited $totalAwaited for color $targetColor at $resolvedCoords")
    }
}

suspend fun Adb.buttonState(button: Button): ButtonState = pixelColor(button.colorLocation).toState(button.colors)

private fun Color.toState(buttonColors: ButtonStateColors) = when(this) {
    buttonColors.enabled -> ButtonState.ENABLED
    buttonColors.disabled -> ButtonState.DISABLED
    else -> ButtonState.INVISIBLE
}

suspend fun Adb.pixelColor(coords: CoordsMap.() -> Coords): Color = pixelColor(coords.resolve())

private suspend fun Adb.pixelColor(coords: Coords): Color {
    val image = screenshot()
    return image.colorAt(coords)
}

suspend fun Adb.saveScreenshot(path: Path) = withContext(Dispatchers.IO) {
    ImageIO.write(screenshot(), "png", path.toFile())
}

private fun BufferedImage.colorAt(coords: Coords) = Color(getRGB(coords.x, coords.y).toUInt())
