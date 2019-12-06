package org.hildan.ipm.helper

import java.util.EnumSet

class Galaxy(
    private val longTermBonus: Bonus,
    private val beaconBonus: BeaconBonus,
    private val managers: List<Manager>
) {
    private val planets = PlanetType.values().map { Planet(it) }
    private val planetsByType = planets.associateBy { it.type }

    private val researchedProjects: MutableSet<Project> = EnumSet.noneOf(Project::class.java)
    private val unlockedProjects: MutableSet<Project> = EnumSet.noneOf(Project::class.java)

    fun setLevels(planet: PlanetType, mine: Int, ships: Int, cargo: Int) {
        val p = planetsByType[planet] ?: error("Missing planet $planet!")
        p.mineLevel = mine
        p.shipLevel = ships
        p.cargoLevel = cargo
    }

    fun research(project: Project) {
        researchedProjects.add(project)
        unlockedProjects.addAll(project.children)
    }

    fun assignManager(manager: Manager, planet: PlanetType) {
        planetsByType[planet]!!.managerBonus = manager.planetBonus
    }

    private val Planet.localBeaconBonus: PlanetBonus
        get() = if (Project.BEACON in researchedProjects) beaconBonus[this.type] else PlanetBonus.NONE

    private val Planet.beaconMiningBonus: Double
        get() = localBeaconBonus.mineRate

    // TODO account for manager bonus
    // TODO account for colony bonus
    private val Planet.actualMineRate: Double
        get() = ownMineRate * longTermBonus.globalPlanetBonus.mineRate * beaconMiningBonus

    // TODO account for preferred ore boost project
    private val Planet.actualMineRateByOreType: Map<OreType, Double>
        get() = type.oreDistribution.associate { it.oreType to (actualMineRate * it.ratio) }

    override fun toString(): String = """
        Planets:
            ${planets.map { "${it.type.name}: ${it.actualMineRateByOreType}" }.joinToString("\n            ")}
    """.trimIndent()
}
