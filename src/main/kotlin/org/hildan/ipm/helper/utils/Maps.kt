package org.hildan.ipm.helper.utils

import java.util.EnumMap

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

inline fun <reified K : Enum<K>, V> lazyEnumMap(crossinline computeValue: (K) -> V): LazyMap<K, V> {
    return LazyMap(EnumMap(K::class.java)) { key, _ -> computeValue(key) }
}

inline fun <K, V> lazyHashMap(crossinline computeValue: (K) -> V): LazyMap<K, V> {
    return LazyMap(HashMap()) { key, _ -> computeValue(key) }
}

class LazyMap<K, V>(
    private val map: MutableMap<K, V> = mutableMapOf(),
    private val computeValue: (K, LazyMap<K, V>) -> V
): Map<K, V> by map {

    override fun get(key: K): V = map[key] ?: computeValue(key, this).also { map[key] = it }
}
