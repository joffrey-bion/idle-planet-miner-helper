package org.hildan.ipm.helper

import java.util.EnumMap
import kotlin.reflect.KClass

class EMap<K : Enum<K>, V>(private val map: Map<K, V>) : Map<K, V> by map {

    override operator fun get(key: K): V = map[key] ?: error("Missing enum value $key")

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
