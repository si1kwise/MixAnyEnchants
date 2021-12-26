package util

import data.enchantment.EnchantmentCostMap
import org.bukkit.enchantments.Enchantment

fun Enchantment.getEnchantmentCost(level: Int, reducedCost: Boolean): Int {
    return EnchantmentCostMap.getEnchantmentCost(this, level, reducedCost)
}