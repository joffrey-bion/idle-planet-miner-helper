package org.hildan.ipm.helper.galaxy.resources

import java.time.Duration

fun Int.sec() = Duration.ofSeconds(this.toLong())

fun Int.min() = Duration.ofMinutes(this.toLong())

fun Int.h() = Duration.ofHours(this.toLong())

operator fun Duration.times(n: Int): Duration = multipliedBy(n.toLong())

operator fun Duration.div(n: Int): Duration = if (isZero) Duration.ZERO else dividedBy(n.toLong())

fun Collection<Duration>.sum(): Duration = fold(Duration.ZERO) { d1, d2 -> d1 + d2}
