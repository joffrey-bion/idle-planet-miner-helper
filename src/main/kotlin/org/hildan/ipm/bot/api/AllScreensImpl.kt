package org.hildan.ipm.bot.api

import kotlinx.coroutines.delay
import org.hildan.ipm.bot.ui.*
import org.hildan.ipm.bot.adb.*
import org.hildan.ipm.bot.procedures.waitAndTapPlanetButton
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.resources.OreType

// TODO logging?

class AllScreensImpl(
    private val adb: Adb,
) : GalaxyScreen, PlanetScreen, ColonyDialog, ColonizationBonusOptionsDialog, ResourcesScreen, ProductionScreen,
    RecipeSelectionDialog, ProjectsScreen, ProjectResearchDialog, ManagersScreen, MothershipScreen, SellGalaxyDialog,
    SellGalaxyConfirmationDialog, ArkBonusClaimDialog, RoversDialog, RoverDiscoveriesDialog {

    private suspend inline fun <reified T> tap(noinline coords: PlatonicCoords): T {
        adb.tap(coords)
        // give some time for the UI to update (it lags sometimes)
        // FIXME replace by pixel checks in key places
        delay(20)
        return this as T
    }

    override suspend fun goToResources(): ResourcesScreen = tap { navbar.ore }
    override suspend fun goToProduction(): ProductionScreen = tap { navbar.production }
    override suspend fun goToProjects(): ProjectsScreen = tap { navbar.projects }
    override suspend fun goToManagers(): ManagersScreen = tap { navbar.managers }
    override suspend fun goToMothership(): MothershipScreen = tap { navbar.mothership }

    override suspend fun closeNavPanel(): GalaxyScreen = tap { closeNavPanel }

    override suspend fun tapBalor(): PlanetScreen = tap { planets.balor }
    override suspend fun tapDrasta(): PlanetScreen = tap { planets.drasta }
    override suspend fun tapAnadius(): PlanetScreen = tap { planets.anadius }
    override suspend fun tapDholen(): PlanetScreen = tap { planets.dholen }

    override suspend fun tapPlanet(planet: Planet): PlanetScreen = when (planet) {
        Planet.BALOR -> tapBalor()
        Planet.DRASTA -> tapDrasta()
        Planet.ANADIUS -> tapAnadius()
        Planet.DHOLEN -> tapDholen()
        else -> error("$planet is not visible in the zoomed galaxy screen, sorry")
    }

    override suspend fun tapNext(): PlanetScreen = tap { planetButtons.next }
    override suspend fun tapMine(): PlanetScreen = tap { planetButtons.mine }
    override suspend fun tapShip(): PlanetScreen = tap { planetButtons.ship }
    override suspend fun tapCargo(): PlanetScreen = tap { planetButtons.cargo }

    override suspend fun upgradeMine(): PlanetScreen {
        adb.waitAndTapPlanetButton(Buttons.planet.upgradeMine)
        return this
    }

    override suspend fun upgradeShip(): PlanetScreen {
        adb.waitAndTapPlanetButton(Buttons.planet.upgradeShip)
        return this
    }

    override suspend fun upgradeCargo(): PlanetScreen {
        adb.waitAndTapPlanetButton(Buttons.planet.upgradeCargo)
        return this
    }

    override suspend fun openColony(): ColonyDialog = tap { planetButtons.colony }
    override suspend fun openManager(): ManagersScreen = tap { planetButtons.manager }
    override suspend fun closePlanet(): GalaxyScreen = tap { planetButtons.close }

    override suspend fun tapColonize(): ColonizationBonusOptionsDialog = tap { coloniesDialog.colonizeButton }

    override suspend fun pickMineColonizationBonus(): PlanetScreen {
        adb.tapWhenEnabled(Buttons.colonizeDialog.upgradeMine)
        return this
    }

    override suspend fun startAutoSell(resource: OreType): ResourcesScreen {
        adb.longTap(coords = resource.coords())
        return this
    }

    override suspend fun stopAutoSell(resource: OreType): ResourcesScreen = startAutoSell(resource)

    private fun OreType.coords(): PlatonicCoords = when(this) {
        OreType.COPPER -> ({ ores.copper })
        OreType.IRON -> ({ ores.iron })
        OreType.LEAD -> ({ ores.lead })
        else -> error("Resource $this is not supported")
    }

    override suspend fun buySmelter2(): ProductionScreen = tap { production.slot2RecipeButton }
    override suspend fun tapSmelter1(): RecipeSelectionDialog = tap { production.slot1RecipeButton }
    override suspend fun tapSmelter2(): RecipeSelectionDialog = tap { production.slot2RecipeButton }

    override suspend fun buyRecipe2(): RecipeSelectionDialog = tap { production.recipePicker.recipe2 }
    override suspend fun selectRecipe1(): ProductionScreen = tap { production.recipePicker.recipe1 }
    override suspend fun selectRecipe2(): ProductionScreen = tap { production.recipePicker.recipe2 }

    override suspend fun switchToCrafting(): ProductionScreen = tap { production.craftButton }

    override suspend fun tapProject(project: Project): ProjectResearchDialog = tap(project.coords())
    override suspend fun confirmResearch(): ProjectsScreen {
        adb.tapWhenEnabled(Buttons.projects.research)
        return this
    }

    private fun Project.coords(): PlatonicCoords = when(this) {
        Project.ASTEROID_MINER -> ({ projects.asteroidMiner })
        Project.SMELTER -> ({ projects.smelter })
        Project.MANAGEMENT -> ({ projects.management })
        Project.TELESCOPE_1 -> ({ projects.telescope1 })
        Project.BEACON -> ({ projects.beacon })
        Project.CRAFTER -> ({ projects.crafter })
        else -> error("Project $this is not supported")
    }

    override suspend fun prevPlanet(): ManagersScreen = tap { managers.prevPlanet }
    override suspend fun nextPlanet(): ManagersScreen = tap { managers.nextPlanet }
    override suspend fun assignFirstManager(): ManagersScreen = tap { managers.firstManager }
    override suspend fun removeAssignedManager(): ManagersScreen = tap { managers.assignedManager }

    override suspend fun tapSellGalaxy(): SellGalaxyDialog = tap { mothership.sellGalaxyButton }
    override suspend fun tapRegularSell(): SellGalaxyConfirmationDialog = tap { mothership.sellGalaxyDialog.regularSellButton }
    override suspend fun tapConfirmGalaxySell(): MothershipScreen = tap { mothership.confirmSellDialog.confirmButton }

    override suspend fun isArkBonusPresent(): Boolean = adb.pixelColor { arkBonusIcon } == Colors.arkBonusIcon
    override suspend fun tapArkBonus(): ArkBonusClaimDialog = tap { arkBonusIcon }
    override suspend fun claimArkBonus(): ScreenWithArkBonusVisible = tap { arkClaim }

    override suspend fun isRoverDone(): Boolean = adb.pixelColor { rover.roverDot } == Colors.rover.readyDot
    override suspend fun tapRover(): RoversDialog = tap { rover.roverDot }
    override suspend fun openRoverDiscoveries(): RoverDiscoveriesDialog = tap { rover.roversDialog.claimBonusButton }
    override suspend fun claimRoverBonus(): ScreenWithRoverVisible {
        adb.tapWhenEnabled(Buttons.rover.roverDiscoveriesClaim)
        return this
    }
}
