package org.hildan.ipm.bot

// Tail events:
// adb shell getevent -l

data class Coords(val x: Int, val y: Int)

interface IpmCoords {
    val arkBonusButton: Coords
    val arkClaim: Coords

    val planets: PlanetCoords
    val planetButtons: PlanetButtonsCoords

    val navbar: NavbarCoords

    val ores: OresCoords
    val production: ProductionCoords
    val projects: ProjectCoords
    val managers: ManagersCoords
    val mothership: MothershipCoords
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
    val close: Coords
}

interface ProductionCoords {
    val smeltButton: Coords
    val craftButton: Coords
    val selectRecipe1Button: Coords
    val selectRecipe2Button: Coords

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

    val closeProjects: Coords
    val researchDialogConfirm: Coords
}

interface ManagersCoords {
    val firstManager: Coords
    val next: Coords
}

interface MothershipCoords {
    val sellButton: Coords
    val sellDialog: SellDialogCoords
}

interface SellDialogCoords {
    val sellButton: Coords
    val confirmSellButton: Coords
}

object OnePlus5Coords : IpmCoords {
    override val arkBonusButton = Coords(850, 305)
    override val arkClaim = Coords(450, 960)

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

        override val close = Coords(855, 710)
    }

    override val production = object : ProductionCoords {
        override val smeltButton = Coords(220, 790)
        override val craftButton = Coords(680, 790)
        override val selectRecipe1Button = Coords(260, 1240)
        override val selectRecipe2Button = Coords(640, 1240)

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

        override val closeProjects = Coords(855, 710)
        override val researchDialogConfirm = Coords(460, 1150)
    }

    override val managers = object : ManagersCoords {
        override val firstManager = Coords(200, 1000)
        override val next = Coords(430, 400)
    }

    override val mothership = object : MothershipCoords {
        override val sellButton = Coords(140, 825)
        override val sellDialog = object : SellDialogCoords {
            override val sellButton = Coords(370, 1025)
            override val confirmSellButton = Coords(530, 1040)
        }
    }
}
