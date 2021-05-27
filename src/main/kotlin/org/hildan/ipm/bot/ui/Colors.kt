package org.hildan.ipm.bot.ui

inline class Color(private val argb: UInt) {
    val alpha get() = (argb shr 24).toUByte()
    val red get() = (argb shr 16).toUByte()
    val green get() = (argb shr 8).toUByte()
    val blue get() = argb.toUByte()

    override fun toString(): String = "0x${argb.toString(16)}"
}

data class ButtonStateColors(
    val enabled: Color,
    val disabled: Color,
)

object Colors {
    val arkBonusIcon = Color(0xffffbe4bu)

    val btn3DTeal = ButtonStateColors(enabled = Color(0xff2e8989u), disabled = Color(0xff1c4e52u))
    val btn3DGreen = ButtonStateColors(enabled = Color(0xff498e35u), disabled = Color(0u))
    val btn3DBlue_plainSquare = ButtonStateColors(enabled = Color(0xff1b6d89u), disabled = Color(0xff194044u))
    val btn3DBlue_gridLine = ButtonStateColors(enabled = Color(0xff1b6d89u), disabled = Color(0xffe21e5bu))
    val flatBlueBtn = ButtonStateColors(enabled = Color(0xff5081b2u), disabled = Color(0u)) // never disabled

    object Rover {
        val readyDot = Color(0xffff4444u)
        val roversClaimBonusButton = Colors.btn3DTeal
        val roverDiscoveriesClaimButton = ButtonStateColors(enabled = Color(0xff066d79u), disabled = Color(0xff1e4e52u))
    }
}
