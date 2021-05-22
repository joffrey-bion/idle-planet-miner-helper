@file:OptIn(ExperimentalTime::class)
package org.hildan.ipm.bot

import com.malinskiy.adam.AndroidDebugBridgeClient
import com.malinskiy.adam.AndroidDebugBridgeClientFactory
import com.malinskiy.adam.request.device.Device
import com.malinskiy.adam.request.device.ListDevicesRequest
import com.malinskiy.adam.request.framebuffer.BufferedImageScreenCaptureAdapter
import com.malinskiy.adam.request.framebuffer.ScreenCaptureRequest
import com.malinskiy.adam.request.shell.v2.ShellCommandRequest
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.milliseconds
import kotlin.time.seconds

suspend fun connectFirstAdbDevice(coords: IpmCoords): Adb {
    val adb = AndroidDebugBridgeClientFactory().build()
    val device = adb.execute(request = ListDevicesRequest()).firstOrNull()
        ?: error("No devices found, run 'adb connect localhost:63075' (or maybe other port) to attach bluestack")
    return Adb(adb, device, coords)
}

class Adb(
    val adb: AndroidDebugBridgeClient,
    val device: Device,
    val coords: IpmCoords,
)

suspend fun Adb.tap(coords: IpmCoords.() -> Coords) = tap(this.coords.coords())

suspend fun Adb.tap(coords: Coords) {
    shell("input tap ${coords.x} ${coords.y}")
    delay(100.milliseconds) // avoid blowing up during startup animations with ships
}

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: IpmCoords.() -> Coords) =
    longTap(duration, this.coords.coords())

suspend fun Adb.longTap(duration: Duration = 1000.milliseconds, coords: Coords) {
    shell("input swipe ${coords.x} ${coords.y} ${coords.x} ${coords.y} ${duration.inMilliseconds.toInt()}")
}

private suspend fun Adb.shell(cmd: String) {
    println("adb shell $cmd")
    val result = adb.execute(ShellCommandRequest(cmd), device.serial)
    if (result.exitCode != 0) error("Non-zero exit code ${result.exitCode} for shell command: $cmd")
}

suspend fun Adb.awaitPixelColor(
    targetColor: Color,
    retryDelay: Duration = 200.milliseconds,
    coords: IpmCoords.() -> Coords,
) {
    val totalAwaited = measureTime {
        var color = pixelColor(coords)
        while (color != targetColor) {
            println("Waiting for color $targetColor, got $color")
            delay(retryDelay)
            color = pixelColor(coords)
        }
    }
    if (totalAwaited > 2.seconds) {
       System.err.println(">>> long await time of $totalAwaited")
    }
}

suspend fun Adb.pixelColor(coords: IpmCoords.() -> Coords): Color = pixelColor(this.coords.coords())

@OptIn(ExperimentalUnsignedTypes::class)
suspend fun Adb.pixelColor(coords: Coords): Color {
    val image = adb.execute(ScreenCaptureRequest(BufferedImageScreenCaptureAdapter()), device.serial)
    return Color(image.getRGB(coords.x, coords.y).toUInt())
}
