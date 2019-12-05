package org.hildan.ipm.helper

interface Sellable {
    val baseSellValue: Int
}

enum class OreType(
    override val baseSellValue: Int
) : Sellable {
    COPPER(1),
    IRON(1),
    LEAD(4),
    SILICON(8),
    ALUMINUM(17),
    SILVER(36),
    GOLD(75),
    DIAMOND(160),
    PLATINUM(340)
}

enum class AlloyType(
    val recipeUnlockPrice: Int,
    override val baseSellValue: Int,
    val baseSmeltTimeSeconds: Int
) : Sellable {
    COPPER_BAR(0, 1_450, 20),
    IRON_BAR(3_000, 3_000, 30),
    LEAD_BAR(9_000, 6_100, 40),
    SILICON_BAR(25_000, 12_500, 60),
    ALUMINUM_BAR(75_000, 27_600, 80),
    SILVER(225_000, 60_000, 120),
    GOLD(500_000, 120_000, 180),
    BRONZE(1_000_000, 234_000, 240)
}

enum class Item(
    val recipeUnlockPrice: Int,
    override val baseSellValue: Int,
    val baseCraftTimeSeconds: Int
) : Sellable {
    COPPER_WIRE(0, 10_000, 60),
    IRON_NAIL(20_000, 20_000, 120),
    BATTERY(50_000, 70_000, 240),
    HAMMER(100_000, 135_000, 480),
    GLASS(200_000, 215_000, 720),
    CIRCUIT(400_000, 620_000, 1200),
    LENSE(1_000_000, 1_100_000, 2400)
}
