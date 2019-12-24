package org.hildan.ipm.helper.utils

fun <K, V> Map<K, V>.mergedWith(other: Map<K, V>, combine: (V, V) -> V): Map<K, V> {
    val result = this.toMutableMap()
    for ((k,v) in other) {
        result.merge(k, v, combine)
    }
    return result
}

inline fun <T, K, V> Iterable<T>.associateMerging(
    keyGetter: (T) -> K,
    valueGetter: (T) -> V,
    noinline merge: (V, V) -> V
): Map<K, V> {
    return fold(mutableMapOf()) { m, elt -> m.merge(keyGetter(elt), valueGetter(elt), merge); m }
}
