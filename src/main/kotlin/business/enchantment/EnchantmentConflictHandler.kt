package business.enchantment

import org.bukkit.enchantments.Enchantment

class EnchantmentConflictHandler {

    /**
     * Loop through all enchantments on the sacrificed item and checks:
     * - is there a conflict with one of the targets enchantments
     * - is there a conflict with one of the other enchantments on this item
     */
    fun hasConflictingEnchantments(
        targetEnchantmentToLevelMap: Map<Enchantment, Int>,
        sacrificeEnchantmentToLevelMap: Map<Enchantment, Int>): Boolean {
        return sacrificeEnchantmentToLevelMap.any { enchantment ->
            val currentEnchantment = enchantment.key
            hasEnchantmentConflict(currentEnchantment, targetEnchantmentToLevelMap) ||
                    conflictsWithOtherEnchantmentsOnTheSameItem(currentEnchantment, sacrificeEnchantmentToLevelMap.toMutableMap()
                    )
        }
    }

    // Check if there is the same enchantment with the same level
    fun hasDuplicateEnchantment(
        enchantment: Enchantment,
        level: Int,
        targetEnchantmentToLevelMap: Map<Enchantment, Int>): Boolean {
        return targetEnchantmentToLevelMap.any { otherEnchantment ->
            enchantment == otherEnchantment.key && level == otherEnchantment.value
        }
    }

    /**
     * If there is a conflict on the same item, combining is not allowed
     * and therefore minecraft does not show the result item
     */
    private fun conflictsWithOtherEnchantmentsOnTheSameItem(
        currentEnchantment: Enchantment,
        allEnchantments: MutableMap<Enchantment, Int>): Boolean {
        allEnchantments.remove(currentEnchantment)
        return allEnchantments
            .map { it.key }
            .any {
                currentEnchantment.conflictsWith(it)
            }
    }

    // Check if the given enchantment conflicts with any enchantment on the target
    private fun hasEnchantmentConflict(
        enchantment: Enchantment,
        targetEnchantmentToLevelMap: Map<Enchantment, Int>): Boolean {
        return targetEnchantmentToLevelMap
            .map { it.key }
            .any { otherEnchantment ->
                enchantment.conflictsWith(otherEnchantment)
            }
    }
}