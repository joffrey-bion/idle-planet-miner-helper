@file:OptIn(ExperimentalTime::class, ExperimentalUnsignedTypes::class)
package org.hildan.ipm.bot.adb

import com.malinskiy.adam.AndroidDebugBridgeClient
import com.malinskiy.adam.AndroidDebugBridgeClientFactory
import com.malinskiy.adam.request.device.Device
import com.malinskiy.adam.request.device.ListDevicesRequest
import com.malinskiy.adam.request.framebuffer.BufferedImageScreenCaptureAdapter
import com.malinskiy.adam.request.framebuffer.ScreenCaptureRequest
import com.malinskiy.adam.request.shell.v2.ShellCommandRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.hildan.ipm.bot.ui.*
import java.awt.image.BufferedImage
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.milliseconds
import kotlin.time.seconds

private const val DEBUG_LOGS = false

suspend fun connectFirstAdbDevice(coords: CoordsMap): Adb {
    val adb = AndroidDebugBridgeClientFactory().build()
    val device = adb.execute(request = ListDevicesRequest()).firstOrNull()
        ?: error("No devices found, run 'adb connect localhost:63075' (or maybe other port) to attach bluestack")
    return Adb(adb, device, coords)
}

class Adb(
    val adb: AndroidDebugBridgeClient,
    val device: Device,
    val coords: CoordsMap,
) {
    fun PlatonicCoords.resolve() = coords.this()
}

suspend fun Adb.tap(coords: PlatonicCoords) = tap(coords.resolve())

suspend fun Adb.tap(coords: Coords) {
    shell("input tap ${coords.x} ${coords.y}")
    delay(100.milliseconds) // avoid blowing up during startup animations with ships
}

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: PlatonicCoords) =
    longTap(duration, coords.resolve())

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: Coords) {
    shell("input swipe ${coords.x} ${coords.y} ${coords.x} ${coords.y} ${duration.inMilliseconds.toInt()}")
}

private suspend fun Adb.shell(cmd: String) {
    if (DEBUG_LOGS) {
        println("adb shell $cmd")
    }
    val result = adb.execute(ShellCommandRequest(cmd), device.serial)
    if (result.exitCode != 0) error("Non-zero exit code ${result.exitCode} for shell command: $cmd")
}

/**
 * Waits until the given [button] is enabled and then taps it.
 * The color is checked every [retryDelay].
 * If the button doesn't become enabled before [timeout], this method gives up and returns false.
 */
internal suspend fun Adb.tapWhenEnabled(
    button: Button,
    retryDelay: Duration = 200.milliseconds,
    timeout: Duration = Duration.INFINITE,
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
            if (DEBUG_LOGS) {
                println("Waiting for color $targetColor, got $color")
            }
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

suspend fun Adb.pixelColor(coords: CoordsMap.() -> Coords): Color = pixelColor(this.coords.coords())

private suspend fun Adb.pixelColor(coords: Coords): Color {
    val image = takeScreenshot()
    return image.colorAt(coords)
}

private suspend fun Adb.takeScreenshot() = adb.execute(ScreenCaptureRequest(BufferedImageScreenCaptureAdapter()), device.serial)

private fun BufferedImage.colorAt(coords: Coords) = Color(getRGB(coords.x, coords.y).toUInt())
