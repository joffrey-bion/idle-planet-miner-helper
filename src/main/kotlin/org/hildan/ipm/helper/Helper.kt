package org.hildan.ipm.helper

fun main() {

    val bonus = Ships.AURORA + Ships.DAUGHTERSHIP + Ships.ELDERSHIP + Room.ENGINEERING.bonus(7)

    val planets = Planet.values().map { PlanetInstance(it) }

    planets.forEach {
        println("${it.planetData.name}: ores/s = ${it.baseOrePerSecond * bonus.globalPlanetBonus.miningRate}")
    }
}
