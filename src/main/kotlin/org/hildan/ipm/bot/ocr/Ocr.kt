package org.hildan.ipm.bot.ocr

import org.hildan.ipm.bot.ui.Color
import java.awt.image.BufferedImage
import kotlin.math.abs

class ElementImage(
    val image: BufferedImage,
    val value: String,
)

class Ocr(
    private val elementImages: List<ElementImage>,
    private val focusColor: Color,
    private val tolerance: Int = 25,
) {
    fun parse(img: BufferedImage): String =
        img.splitOnIrrelevantColumns().joinToString("") { it.readCharacter() }

    private fun BufferedImage.readCharacter(): String = elementImages.maxByOrNull { matchScore(it.image) }!!.value

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
