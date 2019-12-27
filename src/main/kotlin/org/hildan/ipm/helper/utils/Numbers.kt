@file:Suppress("FunctionName")

package org.hildan.ipm.helper.utils

private const val ONE_K: Long = 1_000

private const val ONE_M: Long = 1_000_000

private const val ONE_B: Long = 1_000_000_000

private const val ONE_T: Long = 1_000_000_000_000

private const val ONE_Q: Long = 1_000_000_000_000_000

fun Int.k(): Long = this * ONE_K

fun Int.M(): Long = this * ONE_M

fun Int.B(): Long = this * ONE_B

fun Int.T(): Long = this * ONE_T

fun Int.q(): Long = this * ONE_Q

fun Double.formatWithSuffix() = when {
    this >= ONE_Q -> formatWithSuffix(ONE_Q, "q")
    this >= ONE_T -> formatWithSuffix(ONE_T, "T")
    this >= ONE_B -> formatWithSuffix(ONE_B, "B")
    this >= ONE_M -> formatWithSuffix(ONE_M, "M")
    this >= ONE_K -> formatWithSuffix(ONE_K, "k")
    else -> formatTwoDigits()
}

private fun Double.formatWithSuffix(multiplier: Long, suffix: String): String =
        (this / multiplier).formatTwoDigits(suffix)

fun Double.formatTwoDigits(suffix: String = ""): String = String.format("%.2f $suffix", this)
