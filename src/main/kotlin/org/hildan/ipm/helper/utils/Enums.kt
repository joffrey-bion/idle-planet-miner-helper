package org.hildan.ipm.helper.utils

import java.util.EnumMap
import java.util.EnumSet
import kotlin.reflect.KClass

class EMap<K : Enum<K>, V>(private val map: Map<K, V>) : Map<K, V> by map {

    override operator fun get(key: K): V = map[key] ?: error("Missing enum value $key")

    override fun toString(): String = map.toString()

    companion object {

        inline fun <K : Enum<K>, V> of(clazz: KClass<K>, createValue: (K) -> V): EMap<K, V> =
                EMap(clazz.java.enumConstants.associateTo(EnumMap<K, V>(clazz.java)) { it to createValue(it) })

        inline fun <reified K : Enum<K>, V> of(createValue: (K) -> V): EMap<K, V> = of(K::class, createValue)
    }
}

inline fun <reified K : Enum<K>, V> Map<K, V>.asEMap() =
        asEMap { throw IllegalArgumentException("Missing value for key $it") }

inline fun <reified K : Enum<K>, V> Map<K, V>.asEMap(createMissingValue: (K) -> V) =
        EMap.of<K, V> { this[it] ?: createMissingValue(it) }

fun <T : Enum<T>> T.nextIn(clazz: KClass<T>): T? {
    val values = clazz.java.enumConstants
    return if (ordinal == values.lastIndex) null else values[ordinal + 1]
}

inline fun <reified T : Enum<T>> T.next(): T? = nextIn(T::class)

fun <T : Enum<T>> T.andBelowIn(clazz: KClass<T>): Set<T> {
    return clazz.java.enumConstants.sliceArray(0..ordinal).toCollection(EnumSet.noneOf(clazz.java))
}

inline fun <reified T : Enum<T>> T.andBelow(): Set<T> = andBelowIn(T::class)
