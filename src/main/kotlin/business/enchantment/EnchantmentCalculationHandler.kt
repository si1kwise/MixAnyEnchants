package business.enchantment

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.Repairable
import util.getEnchantmentCost

class EnchantmentCalculationHandler {

    /**
     * Calculates the cost of the current combined enchantments
     * Each item has a fix cost which is multiplied by level and added to the end cost
     * Each item also has a penalty (the more enchantments the more expensive), which is added to the end cost
     * Renaming an item also adds 1 cost to the end cost
     */
    fun calculateEnchantmentCost(
        enchantmentsToAdd: Map<Enchantment, Int>,
        isRenamed: Boolean,
        targetItemMeta: ItemMeta,
        sacrificeItemMeta: ItemMeta): Int {
        var enchantmentCost = getConflictingEnchantmentsCost(enchantmentsToAdd, sacrificeItemMeta is EnchantmentStorageMeta)
        enchantmentCost += (targetItemMeta as Repairable).repairCost
        enchantmentCost += (sacrificeItemMeta as Repairable).repairCost

        return when(isRenamed) {
            true -> enchantmentCost + 1
            false -> enchantmentCost
        }
    }

    // Returns the total cost of the given enchantments
    private fun getConflictingEnchantmentsCost(
        enchantmentsToAdd: Map<Enchantment, Int>,
        reducedCost: Boolean): Int {
        return enchantmentsToAdd.map { enchantment ->
            enchantment.key.getEnchantmentCost(enchantment.value, reducedCost)
        }.sum()
    }
}