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
    val arkClaim = Button(tapLocation = { arkClaim }, colors = Colors.btn3DGreen)

    object Planet {
        val upgradeMine = Button(tapLocation = { planetButtons.mine }, colors = Colors.btn3DTeal)
        val upgradeShip = Button(tapLocation = { planetButtons.ship }, colors = Colors.btn3DTeal)
        val upgradeCargo = Button(tapLocation = { planetButtons.cargo }, colors = Colors.btn3DTeal)
    }

    object Projects {
        val research = Button(tapLocation = { projects.researchDialogConfirm }, colors = Colors.btn3DBlue_plainSquare)
    }

    object ColonizeDialog {
        val colonize = Button(tapLocation = { coloniesDialog.colonizeButton }, colors = Colors.btn3DTeal)
        val upgradeMine = Button(tapLocation = { coloniesDialog.upgradeMineButton }, colors = Colors.flatBlueBtn)
    }

    object GalaxySellDialog {
        val colonize = Button(tapLocation = { mothership.sellGalaxyDialog.regularSellButton }, colors = Colors.btn3DTeal)
        val upgradeMine = Button(tapLocation = { coloniesDialog.upgradeMineButton }, colors = Colors.flatBlueBtn)
    }

    object Rover {
        val roversClaimBonusButton = Button(
            tapLocation = { rover.roversDialog.claimBonusButton },
            colors = Colors.Rover.roversClaimBonusButton,
        )
        val roverDiscoveriesClaim = Button(
            tapLocation = { rover.roverDiscoveriesDialog.claimButton },
            colors = Colors.Rover.roverDiscoveriesClaimButton,
        )
    }
}
