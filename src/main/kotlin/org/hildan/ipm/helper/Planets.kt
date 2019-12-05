package org.hildan.ipm.helper

data class OrePart(
    val oreType: OreType,
    val ratio: Double
)

enum class Planet(
    val ores: List<OrePart>
) {
    BALOR(listOf(OrePart(OreType.COPPER, 1.0))),
    DRASTA(listOf(OrePart(OreType.COPPER, 0.8), OrePart(OreType.IRON, 0.2))),
    ANADIUS(listOf(OrePart(OreType.COPPER, 0.5), OrePart(OreType.IRON, 0.5))),
    DHOLEN(listOf(OrePart(OreType.IRON, 0.8), OrePart(OreType.LEAD, 0.2))),
    VERR(listOf(OrePart(OreType.COPPER, 0.2), OrePart(OreType.IRON, 0.3), OrePart(OreType.LEAD, 0.5))),
}

data class PlanetInstance(
    val planetData: Planet,
    var miningLevel: Int = 1,
    var shipLevel: Int = 1,
    var cargoLevel: Int = 1
) {
    val baseOrePerSecond: Double
        get() = 0.25 + 0.1 * (miningLevel - 1) + (0.017 * (miningLevel - 1) * (miningLevel - 1))
}
