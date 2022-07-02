package org.hildan.ipm.bot.ui

import org.hildan.ipm.bot.ocr.Ocr
import org.hildan.ipm.bot.ocr.ElementImage
import java.awt.Rectangle
import javax.imageio.ImageIO

object Clips {
    val galaxyValue = Rectangle(85, 190, 270 - 85, 226 - 190)
    val galaxyValueInSellDialog = Rectangle(460, 680, 635 - 460, 715 - 680)
}

object Ocrs {
    val galaxyValueInSellDialog = Ocr(
        refImages = REF_IMGS.map { it.toRefImage("gv-sell-dialog") },
        focusColor = Color(0xff8ec1f2u),
    )
}

private val REF_IMGS = (0..9).map { RefImageResource("$it.png", "$it") } +
    RefImageResource("dot.png", ".") +
    RefImageResource("dollar.png", "$") +
    RefImageResource("M.png", "M") +
    RefImageResource("B.png", "B")

private fun RefImageResource.toRefImage(groupName: String) = ElementImage(
    image = ImageIO.read(RefImageResource::class.java.getResourceAsStream("/ocr/$groupName/$filename")),
    value = value,
)

private class RefImageResource(
    val filename: String,
    val value: String,
)
