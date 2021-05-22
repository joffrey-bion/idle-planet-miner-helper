@file:OptIn(ExperimentalUnsignedTypes::class)
package org.hildan.ipm.bot

inline class Color(private val argb: UInt) {
    override fun toString(): String = "0x${argb.toString(16)}"
}

object Colors {
    val PLANET_BUTTON_ACTIVE = Color(0xff2e8989u)
    val RESEARCH_BUTTON_ACTIVE = Color(0xff1b6d89u)
    val ARK_BONUS_ICON = Color(0xffffbe4bu)
}
