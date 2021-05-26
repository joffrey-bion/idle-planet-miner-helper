package org.hildan.ipm.bot.ui

import org.hildan.ipm.bot.ocr.Ocr
import java.awt.Rectangle

object Clips {
    val galaxyValue = Rectangle(85, 190, 270 - 85, 226 - 190)
    val galaxyValueInSellDialog = Rectangle(460, 680, 635 - 460, 715 - 680)
}

object Ocrs {
    val galaxyValueInSellDialog = Ocr(
        groupName = "gv-sell-dialog",
        focusColor = Color(0xff8ec1f2u),
    )
}
