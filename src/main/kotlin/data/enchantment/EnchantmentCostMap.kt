package data.enchantment

import org.bukkit.enchantments.Enchantment

/**
 * Enchantment to Cost map based on https://minecraft.fandom.com/wiki/Anvil_mechanics
 * Stores the cost (item / book) for an enchantment
 */
class EnchantmentCostMap {

    companion object {
        private val enchantmentToCostMap: Map<Enchantment, Pair<Int, Int>> = mapOf(
            Pair(Enchantment.PROTECTION_ENVIRONMENTAL, Pair(1, 1)),
            Pair(Enchantment.PROTECTION_FIRE, Pair(2, 1)),
            Pair(Enchantment.PROTECTION_FALL, Pair(2, 1)),
            Pair(Enchantment.PROTECTION_EXPLOSIONS, Pair(4, 2)),
            Pair(Enchantment.PROTECTION_PROJECTILE, Pair(2, 1)),
            Pair(Enchantment.THORNS, Pair(8, 4)),
            Pair(Enchantment.OXYGEN, Pair(4, 2)),
            Pair(Enchantment.DEPTH_STRIDER, Pair(4, 2)),
            Pair(Enchantment.WATER_WORKER, Pair(4, 2)),
            Pair(Enchantment.DAMAGE_ALL, Pair(1, 1)),
            Pair(Enchantment.DAMAGE_UNDEAD, Pair(2, 1)),
            Pair(Enchantment.DAMAGE_ARTHROPODS, Pair(2, 1)),
            Pair(Enchantment.KNOCKBACK, Pair(2, 1)),
            Pair(Enchantment.FIRE_ASPECT, Pair(4, 2)),
            Pair(Enchantment.LOOT_BONUS_MOBS, Pair(4, 2)),
            Pair(Enchantment.DIG_SPEED, Pair(1, 1)),
            Pair(Enchantment.SILK_TOUCH, Pair(8, 4)),
            Pair(Enchantment.DURABILITY, Pair(2, 1)),
            Pair(Enchantment.LOOT_BONUS_BLOCKS, Pair(4, 2)),
            Pair(Enchantment.ARROW_DAMAGE, Pair(1, 1)),
            Pair(Enchantment.ARROW_KNOCKBACK, Pair(4, 2)),
            Pair(Enchantment.ARROW_FIRE, Pair(4, 2)),
            Pair(Enchantment.ARROW_INFINITE, Pair(8, 4)),
            Pair(Enchantment.LUCK, Pair(4, 2)),
            Pair(Enchantment.LURE, Pair(4, 2)),
            Pair(Enchantment.FROST_WALKER, Pair(4, 2)),
            Pair(Enchantment.MENDING, Pair(4, 2)),
            Pair(Enchantment.BINDING_CURSE, Pair(8, 4)),
            Pair(Enchantment.VANISHING_CURSE, Pair(8, 4)),
            Pair(Enchantment.IMPALING, Pair(4, 2)),
            Pair(Enchantment.RIPTIDE, Pair(4, 2)),
            Pair(Enchantment.LOYALTY, Pair(1, 1)),
            Pair(Enchantment.CHANNELING, Pair(8, 4)),
            Pair(Enchantment.MULTISHOT, Pair(4, 2)),
            Pair(Enchantment.PIERCING, Pair(1, 1)),
            Pair(Enchantment.QUICK_CHARGE, Pair(2, 1)),
            Pair(Enchantment.SOUL_SPEED, Pair(8, 4)),
            Pair(Enchantment.SWEEPING_EDGE, Pair(4, 2)),
        )

        fun getEnchantmentCost(enchantment: Enchantment, level: Int, reducedCost: Boolean) : Int {
            return when (reducedCost) {
                true -> enchantmentToCostMap[enchantment]?.second?.times(level) ?: 0
                false -> enchantmentToCostMap[enchantment]?.first?.times(level) ?: 0
            }
        }
    }
}