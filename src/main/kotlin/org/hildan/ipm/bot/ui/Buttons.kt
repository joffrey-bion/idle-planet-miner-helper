package org.hildan.ipm.bot.ui

data class Button(
    val tapLocation: PlatonicCoords,
    val colorLocation: PlatonicCoords = tapLocation,
    val colors: ButtonStateColors,
)

enum class ButtonState {
    ENABLED,
    DISABLED,
    INVISIBLE,
}

object Buttons {
    val planet = PlanetButtons
    val projects = ProjectsButtons
    val colonizeDialog = ColonizeDialogButtons
    val galaxySellDialog = GalaxySellDialogButtons
    val rover = RoverButtons
    val arkClaim = Button(tapLocation = { arkClaim }, colors = Colors.btn3DGreen)
}

object PlanetButtons {
    val upgradeMine = Button(tapLocation = { planetButtons.mine }, colors = Colors.btn3DTeal)
    val upgradeShip = Button(tapLocation = { planetButtons.ship }, colors = Colors.btn3DTeal)
    val upgradeCargo = Button(tapLocation = { planetButtons.cargo }, colors = Colors.btn3DTeal)
}

object ProjectsButtons {
    val research = Button(tapLocation = { projects.researchDialogConfirm }, colors = Colors.btn3DBlue_plainSquare)
}

object ColonizeDialogButtons {
    val colonize = Button(tapLocation = { coloniesDialog.colonizeButton }, colors = Colors.btn3DTeal)
    val upgradeMine = Button(tapLocation = { coloniesDialog.upgradeMineButton }, colors = Colors.flatBlueBtn)
}

object GalaxySellDialogButtons {
    val colonize = Button(tapLocation = { mothership.sellGalaxyDialog.regularSellButton }, colors = Colors.btn3DTeal)
    val upgradeMine = Button(tapLocation = { coloniesDialog.upgradeMineButton }, colors = Colors.flatBlueBtn)
}

object RoverButtons {
    val roverDiscoveriesClaim = Button(
        tapLocation = { rover.roverDiscoveriesDialog.claimButton },
        colors = Colors.rover.roverDiscoveriesClaimButton,
    )
}
