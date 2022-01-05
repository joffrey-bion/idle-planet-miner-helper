package org.hildan.ipm.bot.api

import org.hildan.ipm.bot.adb.Adb
import org.hildan.ipm.bot.ui.ButtonState
import org.hildan.ipm.helper.galaxy.Project
import org.hildan.ipm.helper.galaxy.planets.Planet
import org.hildan.ipm.helper.galaxy.resources.OreType

// - navbar is always visible (even in managers screen, even with dialogs)
// - all navbar panels are the same height, except managers that are full screen
// - when a non-manager panel is open, it leaves all relevant top-right icons visible (even with ark bonus active)
// - ark bonus moves the rover down
// - planet panel (with colony button) leaves 4 icons in the top-right corner (rover never visible, but the ark is)

// top-right icons:
// - settings
// - achievements
// - permanent mining bonus
// - daily gifts (when available, once a day) - FIXME detected as ark bonus!!
// - ark bonus (when available) -
// - daily tasks
// - rover (even when it's not unlocked)
// - tournament (when available)
// - challenge (when available) - TODO check if before tournament

/**
 * Parent of all screens, navbar always visible.
 */
interface BaseScreen {
    val adb: Adb
    suspend fun goToResources(): ResourcesScreen
    suspend fun goToProduction(): ProductionScreen
    suspend fun goToProjects(): ProjectsScreen
    suspend fun goToManagers(): ManagersScreen
    suspend fun goToMothership(): MothershipScreen
}

interface ScreenWithArkBonusVisible : BaseScreen {
    suspend fun isArkBonusPresent(): Boolean
    suspend fun tapArkBonus(): ArkBonusClaimDialog
}

interface ArkBonusClaimDialog {
    suspend fun claimArkBonus(): ScreenWithArkBonusVisible
}

interface ScreenWithRoverVisible {
    suspend fun isRoverDone(): Boolean
    suspend fun tapRover(): RoversDialog
}

interface RoversDialog {
    suspend fun openRoverDiscoveries(): RoverDiscoveriesDialog
}

interface RoverDiscoveriesDialog {
    suspend fun claimRoverBonus(): ScreenWithRoverVisible
}

/**
 * Screen where we can see the top of the galaxy, including Balor, Drasta, and the Ark bonus, but excluding rover
 * bonus.
 */
interface ScreenWithGalaxyTopVisible : BaseScreen, ScreenWithArkBonusVisible {
    suspend fun tapBalor(): PlanetScreen
    suspend fun tapDrasta(): PlanetScreen
}

/**
 * No expanded panel, no dialogs.
 */
interface GalaxyScreen : ScreenWithGalaxyTopVisible, ScreenWithRoverVisible {
    suspend fun tapAnadius(): PlanetScreen
    suspend fun tapDholen(): PlanetScreen
    suspend fun tapPlanet(planet: Planet): PlanetScreen
}

interface SmallNavPanelExpandedScreen : ScreenWithGalaxyTopVisible, ScreenWithRoverVisible {
    /** Closes the currently open panel */
    suspend fun closeNavPanel(): GalaxyScreen
}

interface PlanetScreen : ScreenWithGalaxyTopVisible {
    suspend fun nextPlanet(): PlanetScreen

    /** Simply taps the mine button even if not enabled */
    suspend fun tapMine(): PlanetScreen
    /** Simply taps the ship button even if not enabled */
    suspend fun tapShip(): PlanetScreen
    /** Simply taps the cargo button even if not enabled */
    suspend fun tapCargo(): PlanetScreen

    /** Actually waits for the mine button to be enabled, and taps */
    suspend fun upgradeMine(): PlanetScreen
    /** Actually waits for the ship button to be enabled, and taps */
    suspend fun upgradeShip(): PlanetScreen
    /** Actually waits for the cargo button to be enabled, and taps */
    suspend fun upgradeCargo(): PlanetScreen

    suspend fun openColony(): ColonyDialog
    suspend fun openManager(): ManagersScreen
    suspend fun closePlanet(): GalaxyScreen
}

interface ColonyDialog : BaseScreen {
    suspend fun readColonyButtonState(): ButtonState
    suspend fun tapColonize(): ColonizationBonusOptionsDialog
    suspend fun nextColonizedPlanet(): ColonyDialog
}

interface ColonizationBonusOptionsDialog : BaseScreen {
    suspend fun pickMineColonizationBonus(): ColonyDialog
}

interface ResourcesScreen : SmallNavPanelExpandedScreen {
    suspend fun startAutoSell(resource: OreType): ResourcesScreen
    suspend fun stopAutoSell(resource: OreType): ResourcesScreen
    suspend fun closeResources() = closeNavPanel()
}

interface ProductionScreen : SmallNavPanelExpandedScreen {
    suspend fun buySmelter2(): ProductionScreen // assuming SMELT tab && smelter 2 NOT bought
    suspend fun tapSmelter1(): RecipeSelectionDialog // assuming SMELT tab
    suspend fun tapSmelter2(): RecipeSelectionDialog // assuming SMELT tab && smelter 2 bought

    suspend fun buyCrafter2(): ProductionScreen = buySmelter2() // assuming CRAFT tab && crafter 2 NOT bought
    suspend fun tapCrafter1(): RecipeSelectionDialog = tapSmelter1() // assuming CRAFT tab
    suspend fun tapCrafter2(): RecipeSelectionDialog = tapSmelter2() // assuming CRAFT tab

    suspend fun switchToCrafting(): ProductionScreen // assuming SMELT tab
    suspend fun closeProduction() = closeNavPanel()
}

interface RecipeSelectionDialog : BaseScreen {
    suspend fun buyRecipe2(): RecipeSelectionDialog // assuming recipe 2 NOT bought
    suspend fun selectRecipe1(): ProductionScreen
    suspend fun selectRecipe2(): ProductionScreen // assuming recipe 2 bought
}

interface ProjectsScreen : SmallNavPanelExpandedScreen {
    suspend fun tapProject(project: Project): ProjectResearchDialog
    suspend fun closeProjects() = closeNavPanel()
}

interface ProjectResearchDialog : BaseScreen {
    suspend fun confirmResearch(): ProjectsScreen
}

interface ManagersScreen : BaseScreen {
    suspend fun prevManagedPlanet(): ManagersScreen
    suspend fun nextManagedPlanet(): ManagersScreen
    suspend fun assignFirstManager(): ManagersScreen
    suspend fun removeAssignedManager(): ManagersScreen
}

interface MothershipScreen : SmallNavPanelExpandedScreen {
    suspend fun tapSellGalaxy(): SellGalaxyDialog
    suspend fun closeMothership() = closeNavPanel()
}

interface SellGalaxyDialog : BaseScreen {
    suspend fun tapRegularSell(): SellGalaxyConfirmationDialog
}

interface SellGalaxyConfirmationDialog : BaseScreen {
    suspend fun tapConfirmGalaxySell(): MothershipScreen
}
