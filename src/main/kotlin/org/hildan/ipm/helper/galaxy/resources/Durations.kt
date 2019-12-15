package org.hildan.ipm.helper.galaxy.resources

import java.time.Duration

fun Int.sec() = Duration.ofSeconds(this.toLong())

fun Int.min() = Duration.ofMinutes(this.toLong())

fun Int.h() = Duration.ofHours(this.toLong())
