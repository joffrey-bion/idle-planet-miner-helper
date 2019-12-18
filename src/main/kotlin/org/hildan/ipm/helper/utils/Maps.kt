package org.hildan.ipm.helper.utils

fun <K, V> Map<K, V>.mergedWith(other: Map<K, V>, combine: (V, V) -> V): Map<K, V> {
    val result = this.toMutableMap()
    for ((k,v) in other) {
        result.merge(k, v, combine)
    }
    return result
}
