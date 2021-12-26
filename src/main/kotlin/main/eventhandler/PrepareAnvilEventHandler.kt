package main.eventhandler

import business.enchantment.EnchantmentCalculationHandler
import business.enchantment.EnchantmentConflictHandler
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.Repairable
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow

/**
 * Event Handler to allow combine not allowed enchantments on an item
 * Repair cost is calculated based on the default calculation in minecraft
 * https://minecraft.fandom.com/wiki/Anvil_mechanics
 */
class PrepareAnvilEventHandler(
    private val javaPlugin: JavaPlugin,
    private val enchantmentCalculationHandler: EnchantmentCalculationHandler,
    private val enchantmentConflictHandler: EnchantmentConflictHandler
) : Listener {

    // TODO name change bug
    @EventHandler
    fun onAnvilContentChange(event: PrepareAnvilEvent) {
        val anvilInventory = event.inventory
        if (allViewersHavePermissions(anvilInventory.viewers)) {
            val targetItem = anvilInventory.getItem(0)
            val sacrificeItem = anvilInventory.getItem(1)
            var resultItem = event.result

            if (targetItem != null && sacrificeItem != null) {
                val targetItemMeta = targetItem.itemMeta
                val sacrificeItemMeta = sacrificeItem.itemMeta

                if (targetItemMeta != null && sacrificeItemMeta != null) {
                    val targetEnchantmentToLevelMap = getEnchantments(targetItemMeta)
                    val sacrificeEnchantmentToLevelMap = getEnchantments(sacrificeItemMeta)
                    // gets a list of not allowed enchantments
                    val hasConflictingEnchantments =
                        enchantmentConflictHandler.hasConflictingEnchantments(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap)

                    if (hasConflictingEnchantments) {
                        if (targetItemMeta !is EnchantmentStorageMeta || sacrificeItemMeta is EnchantmentStorageMeta) {
                            // if combining not allowed enchantments, the result is null because it is not allowed
                            // so we have to manually set the target item as the result item
                            // and manually add the not allowed enchantments to it
                            if (resultItem == null || resultItem.type.isAir) {
                                resultItem = targetItem.clone()
                            }

                            val resultItemMeta = resultItem.itemMeta
                            if (resultItemMeta != null) {
                                // for each enchantment, the item gets a penalty (higher experience costs when repairing)
                                // The formula for prior use penalty is:
                                // (prior use penalty) = 2^(anvil use count) - 1
                                // this imitates the default minecraft behavior
                                if (resultItemMeta is Repairable) {
                                    val amountOfEnchantments = when (resultItemMeta) {
                                        is EnchantmentStorageMeta -> sacrificeEnchantmentToLevelMap.size
                                        else -> targetEnchantmentToLevelMap.size + sacrificeEnchantmentToLevelMap.size
                                    }

                                    resultItemMeta.repairCost = (2.0.pow(amountOfEnchantments.toDouble()) - 1).toInt()
                                }

                                // check if there is the same enchantment with the same level
                                val newEnchantmentToLevelMap =
                                    getEnchantmentsToAdd(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap)

                                storeNewEnchantmentToLevelMapOnItem(resultItemMeta, newEnchantmentToLevelMap)

                                resultItem.itemMeta = resultItemMeta

                                val enchantmentCost = enchantmentCalculationHandler.calculateEnchantmentCost(
                                    newEnchantmentToLevelMap,
                                    isRenamed(anvilInventory),
                                    targetItemMeta,
                                    sacrificeItemMeta)

                                updateAnvilInventoryCostProperty(anvilInventory, enchantmentCost)
                            }
                        }
                    }
                }
            } else if (targetItem != null && sacrificeItem == null) {
                resultItem = targetItem.clone()
            } else {
                resultItem = ItemStack(Material.AIR)
            }

            resultItem?.itemMeta?.let { itemMeta ->
                itemMeta.setDisplayName(anvilInventory.renameText)
                resultItem.itemMeta = itemMeta
            }

            event.result = resultItem
        }
    }

    // Store new enchantment to level map on the result item
    private fun storeNewEnchantmentToLevelMapOnItem(
        resultItemMeta: ItemMeta,
        newEnchantmentToLevelMap: Map<Enchantment, Int>) {
        // store each conflicting enchantment in the item meta if it is a type of EnchantmentStorageMeta
        // this is important so the item meta matches the enchantments on the item
        if (resultItemMeta is EnchantmentStorageMeta) {
            newEnchantmentToLevelMap.forEach {
                resultItemMeta.addStoredEnchant(it.key, it.value, false)
            }
        } else {
            newEnchantmentToLevelMap.forEach {
                resultItemMeta.addEnchant(it.key, it.value, false)
            }
        }
    }

    // Get a list of enchantments with the correct level
    private fun getEnchantmentsToAdd(
        targetEnchantmentToLevelMap: Map<Enchantment, Int>,
        sacrificeEnchantmentToLevelMap: Map<Enchantment, Int>): Map<Enchantment, Int> {
        return sacrificeEnchantmentToLevelMap.mapValues {
            if (enchantmentConflictHandler.hasDuplicateEnchantment(it.key, it.value, targetEnchantmentToLevelMap)) {
                it.value + 1
            } else {
                it.value
            }
        }
    }

    // Check if the item is renamed
    private fun isRenamed(anvilInventory: AnvilInventory): Boolean {
        return !anvilInventory.renameText.isNullOrEmpty()
    }

    // Update the cost of current combined enchantments in the window
    private fun updateAnvilInventoryCostProperty(inventory: AnvilInventory, cost: Int) {
        javaPlugin.server.scheduler.runTask(javaPlugin, Runnable {
            inventory.repairCost = cost
            for (viewer in inventory.viewers) {
                (viewer as? Player)?.setWindowProperty(InventoryView.Property.REPAIR_COST, cost)
            }
        })
    }

    // Check if all players have a permission
    private fun allViewersHavePermissions(viewers: List<HumanEntity>): Boolean {
        return viewers.all { viewer ->
            viewer.hasPermission("mixanyenchant.use")
        }
    }

    // Get all enchantments from an item / a book
    private fun getEnchantments(itemMeta: ItemMeta): Map<Enchantment, Int> {
        return when {
            itemMeta is EnchantmentStorageMeta -> itemMeta.storedEnchants
            itemMeta.hasEnchants() -> itemMeta.enchants
            else -> emptyMap()
        }
    }
}