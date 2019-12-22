package org.hildan.ipm.helper.utils

import java.time.Duration

val INFINITE_TIME: Duration = Duration.ofDays(10000)

fun max(d1: Duration, d2: Duration): Duration = if (d1 > d2) d1 else d2
