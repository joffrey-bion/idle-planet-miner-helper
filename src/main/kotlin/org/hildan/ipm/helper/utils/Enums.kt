package org.hildan.ipm.helper.utils

import java.util.EnumMap
import java.util.EnumSet

inline fun <reified K : Enum<K>, V> completeEnumMap(createValue: (key: K) -> V): EnumMap<K, V> {
    val javaEnumClass = K::class.java
    val result = EnumMap<K, V>(javaEnumClass)
    return javaEnumClass.enumConstants.associateTo(result) { it to createValue(it) }
}

inline fun <reified K : Enum<K>, V> Map<K, V>.completedBy(createMissingValue: (K) -> V): EnumMap<K, V> =
        completeEnumMap { this[it] ?: createMissingValue(it) }

inline fun <reified T : Enum<T>> T.next(): T? {
    val values = T::class.java.enumConstants
    return if (ordinal == values.lastIndex) null else values[ordinal + 1]
}

inline fun <reified T : Enum<T>> T.andBelow(): EnumSet<T> =
        T::class.java.enumConstants.sliceArray(0..ordinal).toCollection(EnumSet.noneOf(T::class.java))
