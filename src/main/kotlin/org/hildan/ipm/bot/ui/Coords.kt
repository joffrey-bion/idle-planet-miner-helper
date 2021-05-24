package org.hildan.ipm.bot.ui

data class Coords(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}

typealias PlatonicCoords = CoordsMap.() -> Coords

interface CoordsMap {
    val arkBonusIcon: Coords
    val arkClaim: Coords
    val rover: RoverCoords

    val planets: PlanetCoords
    val planetButtons: PlanetButtonsCoords

    val navbar: NavbarCoords
    val closeNavPanel: Coords

    val ores: OresCoords
    val production: ProductionCoords
    val projects: ProjectCoords
    val managers: ManagersCoords
    val mothership: MothershipCoords
    val coloniesDialog: ColoniesCoords
}

interface PlanetCoords {
    val balor: Coords
    val drasta: Coords
    val anadius: Coords
    val dholen: Coords
}

interface PlanetButtonsCoords {
    val mine: Coords
    val ship: Coords
    val cargo: Coords
    val manager: Coords
    val next: Coords
    val close: Coords
    val colony: Coords
}

interface ColoniesCoords {
    val colonizeButton: Coords
    val upgradeMineButton: Coords
    val nextPlanet: Coords
    val close: Coords
}

interface NavbarCoords {
    val ore: Coords
    val production: Coords
    val projects: Coords
    val managers: Coords
    val mothership: Coords
}

interface OresCoords {
    val copper: Coords
    val iron: Coords
    val lead: Coords
}

interface ProductionCoords {
    val smeltButton: Coords
    val craftButton: Coords
    val slot1RecipeButton: Coords
    val slot2RecipeButton: Coords

    val recipePicker: RecipePickerCoords
}

interface RecipePickerCoords {
    val recipe1: Coords
    val recipe2: Coords
}

interface ProjectCoords {
    val asteroidMiner: Coords
    val smelter: Coords
    val management: Coords
    val telescope1: Coords
    val beacon: Coords
    val crafter: Coords

    val researchDialogConfirm: Coords
}

interface ManagersCoords {
    val firstManager: Coords
    val assignedManager: Coords
    val nextPlanet: Coords
    val prevPlanet: Coords
}

interface MothershipCoords {
    val sellGalaxyButton: Coords
    val sellGalaxyDialog: GalaxySellDialogCoords
    val confirmSellDialog: ConfirmSellDialogCoords
}

interface GalaxySellDialogCoords {
    val regularSellButton: Coords
}

interface ConfirmSellDialogCoords {
    val confirmButton: Coords
}

interface RoverCoords {
    val roverDot: Coords
    val roversDialog: RoversDialogCoords
    val roverDiscoveriesDialog: RoverDiscoveriesDialogCoords
}

interface RoversDialogCoords {
    val claimBonusButton: Coords
}

interface RoverDiscoveriesDialogCoords {
    val claimButton: Coords
}

object OnePlus5CoordsMap : CoordsMap {
    override val arkBonusIcon = Coords(850, 305)
    override val arkClaim = Coords(450, 960)

    override val closeNavPanel = Coords(855, 710)

    override val planets = object : PlanetCoords {
        override val balor = Coords(230, 230)
        override val drasta = Coords(640, 90)
        override val anadius = Coords(750, 1050)
        override val dholen = Coords(30, 1200)
    }

    override val planetButtons = object : PlanetButtonsCoords {
        override val mine = Coords(640, 1115) // not on text
        override val ship = Coords(640, 1260) // not on text
        override val cargo = Coords(640, 1405) // not on text
        override val manager = Coords(810, 600)
        override val next = Coords(340, 600)
        override val close = Coords(850, 470)
        override val colony = Coords(840, 415)
    }

    override val coloniesDialog = object : ColoniesCoords {
        override val colonizeButton = Coords(350, 1350)
        override val upgradeMineButton = Coords(630, 1000)
        override val nextPlanet = Coords(570, 410)
        override val close = Coords(730, 200)
    }

    override val navbar = object : NavbarCoords {
        override val ore = Coords(75, 1560)
        override val production = Coords(230, 1560)
        override val projects = Coords(380, 1560)
        override val managers = Coords(530, 1560)
        override val mothership = Coords(820, 1560)
    }

    override val ores = object : OresCoords {
        override val copper = Coords(350, 880)
        override val iron = Coords(350, 975)
        override val lead = Coords(350, 1070)
    }

    override val production = object : ProductionCoords {
        override val smeltButton = Coords(220, 790)
        override val craftButton = Coords(680, 790)
        override val slot1RecipeButton = Coords(260, 1240)
        override val slot2RecipeButton = Coords(640, 1240)

        override val recipePicker = object : RecipePickerCoords {
            override val recipe1 = Coords(320, 600)
            override val recipe2 = Coords(600, 600)
        }
    }

    override val projects = object : ProjectCoords {
        override val asteroidMiner = Coords(310, 1160)
        override val smelter = Coords(450, 1300)
        override val management = Coords(580, 1160)
        override val telescope1 = Coords(450, 1010)
        override val beacon = Coords(290, 1010)
        override val crafter = Coords(360, 1450)

        override val researchDialogConfirm = Coords(460, 1150)
    }

    override val managers = object : ManagersCoords {
        override val firstManager = Coords(200, 1000)
        override val assignedManager = Coords(635, 380) // in the headlight of manager, or in the background when none
        override val prevPlanet = Coords(100, 400)
        override val nextPlanet = Coords(430, 400)
    }

    override val mothership = object : MothershipCoords {
        override val sellGalaxyButton = Coords(140, 825)
        override val sellGalaxyDialog = object : GalaxySellDialogCoords {
            override val regularSellButton = Coords(370, 1025)
        }
        override val confirmSellDialog = object : ConfirmSellDialogCoords {
            override val confirmButton = Coords(530, 1040)
        }
    }

    override val rover = object : RoverCoords {
        override val roverDot = Coords(875, 460)
        override val roversDialog = object : RoversDialogCoords {
            override val claimBonusButton = Coords(720, 590) // not on text
        }
        override val roverDiscoveriesDialog = object : RoverDiscoveriesDialogCoords {
            override val claimButton = Coords(540, 1030) // not on text
        }
    }
}
