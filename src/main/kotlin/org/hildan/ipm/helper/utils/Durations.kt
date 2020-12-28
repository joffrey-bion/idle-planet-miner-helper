package org.hildan.ipm.helper.utils

import kotlin.time.Duration

// preferred instead of Kotlin's kotlin.comparisons.maxOf(T, T) to avoid boxing the Duration inline class
fun fastMaxOf(d1: Duration, d2: Duration): Duration = if (d1 > d2) d1 else d2

inline fun <T> Collection<T>.sumBy(extractDuration: (T) -> Duration): Duration =
        fold(Duration.ZERO) { acc, elt -> acc + extractDuration(elt) }
