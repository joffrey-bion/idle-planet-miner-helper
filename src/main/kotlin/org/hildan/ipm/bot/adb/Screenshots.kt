package org.hildan.ipm.bot.adb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.hildan.ipm.bot.ui.Button
import org.hildan.ipm.bot.ui.ButtonState
import org.hildan.ipm.bot.ui.ButtonStateColors
import org.hildan.ipm.bot.ui.Color
import org.hildan.ipm.bot.ui.Coords
import org.hildan.ipm.bot.ui.PlatonicCoords
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.time.Duration
import kotlin.time.milliseconds

suspend fun Adb.awaitPixelColor(
    targetColor: Color,
    pollingPeriod: Duration = 200.milliseconds,
    coords: PlatonicCoords,
) {
    val resolvedCoords = coords.resolve()
    while (pixelColorAt(resolvedCoords) != targetColor) {
        delay(pollingPeriod)
    }
}

suspend fun Adb.pixelColorAt(coords: PlatonicCoords): Color = pixelColorAt(coords.resolve())

private suspend fun Adb.pixelColorAt(coords: Coords): Color = screenshot().colorAt(coords)

private fun BufferedImage.colorAt(coords: Coords) = Color(getRGB(coords.x, coords.y).toUInt())

suspend fun Adb.buttonState(button: Button): ButtonState = pixelColorAt(button.colorLocation).toState(button.colors)

private fun Color.toState(buttonColors: ButtonStateColors) = when(this) {
    buttonColors.enabled -> ButtonState.ENABLED
    buttonColors.disabled -> ButtonState.DISABLED
    else -> ButtonState.INVISIBLE
}

fun BufferedImage.clippedTo(clipRectangle: Rectangle): BufferedImage =
    getSubimage(clipRectangle.x, clipRectangle.y, clipRectangle.width, clipRectangle.height)

suspend fun BufferedImage.saveTo(path: Path, format: String = "png") {
    withContext(Dispatchers.IO) {
        ImageIO.write(this@saveTo, format, path.toFile())
    }
}
