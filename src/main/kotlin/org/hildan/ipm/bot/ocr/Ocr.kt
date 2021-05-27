package org.hildan.ipm.bot.ocr

import org.hildan.ipm.bot.adb.saveTo
import org.hildan.ipm.bot.ui.Color
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.math.abs

private val REF_IMGS = (0..9).map { RefImage("$it.png", "$it") } +
        RefImage("dot.png", ".") +
        RefImage("dollar.png", "$") +
        RefImage("M.png", "M") +
        RefImage("B.png", "B")

private class RefImage(
    val filename: String,
    val value: String,
)

fun BufferedImage.ocr(ocr: Ocr) = ocr.parse(this)

class Ocr(
    private val groupName: String,
    private val focusColor: Color,
    private val tolerance: Int = 25,
) {
    private val RefImage.img: BufferedImage
        get() = ImageIO.read(RefImage::class.java.getResourceAsStream("/ocr/$groupName/$filename"))

    fun parse(img: BufferedImage): String =
        img.splitOnIrrelevantColumns().joinToString("") { it.readCharacter() }

    private fun BufferedImage.readCharacter(): String = REF_IMGS.maxByOrNull { matchScore(it.img) }!!.value

    suspend fun savePartsTo(image: BufferedImage, destinationDir: Path) {
        image.splitOnIrrelevantColumns().forEachIndexed { index, subImg ->
            subImg.saveTo(destinationDir.resolve("img-part-$index-${UUID.randomUUID()}.png"))
        }
    }

    private fun BufferedImage.matchScore(reference: BufferedImage): Double {
        if (height != reference.height || width != reference.width) {
            return 0.0
        }
        var score = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (argb(i, j).isRelevant() == reference.argb(i, j).isRelevant()) {
                    score++
                }
            }
        }
        return score.toDouble() / (width * height)
    }

    private fun BufferedImage.splitOnIrrelevantColumns(): List<BufferedImage> =
        getIrrelevantColumnRanges().map { colRange -> subImage(colRange) }

    @OptIn(ExperimentalStdlibApi::class)
    private fun BufferedImage.getIrrelevantColumnRanges(): List<IntRange> {
        val rangeBounds = buildList {
            add(-1)
            addAll(getIrrelevantColumnsIndices())
            add(width)
        }
        return rangeBounds.zipWithNext { start, end -> (start + 1) until end }.filter { !it.isEmpty() }
    }

    private fun BufferedImage.getIrrelevantColumnsIndices() = (0 until width).filter { col -> !isRelevantColumn(col) }

    private fun BufferedImage.isRelevantColumn(col: Int) = (0 until height).any { row -> argb(col, row).isRelevant() }

    private fun Color.isRelevant(): Boolean = closeEnough(alpha, focusColor.alpha) //
            && closeEnough(red, focusColor.red) //
            && closeEnough(green, focusColor.green) //
            && closeEnough(blue, focusColor.blue)

    private fun closeEnough(a: UByte, b: UByte) = abs(a.toInt() - b.toInt()) < tolerance
}

private fun BufferedImage.subImage(colRange: IntRange): BufferedImage =
    getSubimage(colRange.first, 0, colRange.last - colRange.first + 1, height)

private fun BufferedImage.argb(col: Int, row: Int) = Color(getRGB(col, row).toUInt())
