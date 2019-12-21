package org.hildan.ipm.helper.galaxy.resources

import java.time.Duration

fun Int.sec(): Duration = Duration.ofSeconds(this.toLong())

fun Int.min(): Duration = Duration.ofMinutes(this.toLong())

fun Int.h(): Duration = Duration.ofHours(this.toLong())

operator fun Duration.times(n: Int): Duration = multipliedBy(n.toLong())

operator fun Duration.div(n: Int): Duration = if (isZero) Duration.ZERO else dividedBy(n.toLong())

inline fun <T> Collection<T>.sumBy(extractDuration: (T) -> Duration): Duration = fold(Duration.ZERO) { acc, elt ->
    acc + extractDuration(elt)
}
