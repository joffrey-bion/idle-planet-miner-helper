package org.hildan.ipm.bot.adb

import com.malinskiy.adam.AndroidDebugBridgeClient
import com.malinskiy.adam.AndroidDebugBridgeClientFactory
import com.malinskiy.adam.request.device.Device
import com.malinskiy.adam.request.device.DeviceState
import com.malinskiy.adam.request.device.ListDevicesRequest
import com.malinskiy.adam.request.framebuffer.BufferedImageScreenCaptureAdapter
import com.malinskiy.adam.request.framebuffer.ScreenCaptureRequest
import com.malinskiy.adam.request.shell.v2.ShellCommandRequest
import kotlinx.coroutines.delay
import org.hildan.ipm.bot.ui.CoordsMap
import org.hildan.ipm.bot.ui.PlatonicCoords
import java.net.ConnectException
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val NO_DEVICE_ERROR = "No connected device found, please run 'adb connect localhost:<BS_PORT>' to " +
        "attach Bluestacks to the adb server. You can find the port in Bluestacks Settings > Preferences > Platform " +
        "settings. Make sure you tick 'Enable Android Debug Bridge (ABD)', the port is displayed in the subtext."

class Adb(
    private val client: AndroidDebugBridgeClient,
    private val device: Device,
    private val coords: CoordsMap,
    private val touchRegistrationSafetyDelay: Duration = 100.milliseconds
) {
    fun PlatonicCoords.resolve() = coords.this()

    suspend fun shell(cmd: String) {
        val result = client.execute(ShellCommandRequest(cmd), device.serial)
        delay(touchRegistrationSafetyDelay) // ensure command is registered by the device
        if (result.exitCode != 0) error("Non-zero exit code ${result.exitCode} for shell command: $cmd")
    }

    suspend fun screenshot() = client.execute(ScreenCaptureRequest(BufferedImageScreenCaptureAdapter()), device.serial)

    companion object {
        suspend fun connectToFirstDevice(coordsMap: CoordsMap): Adb {
            val client = AndroidDebugBridgeClientFactory().build()
            val device = client.firstConnectedDevice()
            println("Connected to device ${device.serial}")
            return Adb(client, device, coordsMap)
        }
    }
}

private suspend fun AndroidDebugBridgeClient.firstConnectedDevice(): Device {
    val device = listDevices().firstOrNull { it.state == DeviceState.DEVICE }
    if (device == null) {
        System.err.println(NO_DEVICE_ERROR)
        exitProcess(1)
    }
    return device
}

private suspend fun AndroidDebugBridgeClient.listDevices(): List<Device> = try {
    execute(request = ListDevicesRequest())
} catch (e: ConnectException) {
    error("No adb server running, please run 'adb connect localhost:<BS_PORT>' to start the adb server and attach Bluestacks to it.")
}
