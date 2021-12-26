package business.enchantment

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.Repairable
import org.junit.jupiter.api.Test

class EnchantmentCalculationHandlerTest {

    private val enchantmentCalculationHandler: EnchantmentCalculationHandler = EnchantmentCalculationHandler()

    @Test
    fun calculateEnchantmentCost_withSimpleEnchantment() {
        // ARROW_INFINITE       8
        // renaming             0
        // target penalty       0
        // sacrifice penalty    0
        // cost                 8

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.ARROW_INFINITE, 1))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))

        every { (targetItemMeta as Repairable).repairCost } returns 0
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, false, targetItemMeta, sacrificeItemMeta, 8)
    }

    @Test
    fun calculateEnchantmentCost_withMultipleEnchantments() {
        // ARROW_INFINITE       8
        // IMPALING             8
        // renaming             0
        // target penalty       0
        // sacrifice penalty    0
        // cost                 16

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.ARROW_INFINITE, 1),
            Pair(Enchantment.IMPALING, 2))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))

        every { (targetItemMeta as Repairable).repairCost } returns 0
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, false, targetItemMeta, sacrificeItemMeta, 16)
    }

    @Test
    fun calculateEnchantmentCost_withMultipleEnchantments_renamed() {
        // ARROW_INFINITE       8
        // IMPALING             8
        // renaming             1
        // target penalty       0
        // sacrifice penalty    0
        // cost                 17

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.ARROW_INFINITE, 1),
            Pair(Enchantment.IMPALING, 2))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))

        every { (targetItemMeta as Repairable).repairCost } returns 0
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, true, targetItemMeta, sacrificeItemMeta, 17)
    }

    @Test
    fun calculateEnchantmentCost_withSimpleEnchantment_penaltyOnTargetItem() {
        // LUCK                 4
        // renaming             0
        // target penalty       1
        // sacrifice penalty    0
        // cost                 5

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.LUCK, 1))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))

        every { (targetItemMeta as Repairable).repairCost } returns 1
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, false, targetItemMeta, sacrificeItemMeta, 5)
    }

    @Test
    fun calculateEnchantmentCost_withSimpleEnchantment_penaltyOnTargetItem_and_penaltyOnSacrificeItem() {
        // LUCK                 4
        // renaming             0
        // target penalty       1
        // sacrifice penalty    3
        // cost                 8

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.LUCK, 1))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))

        every { (targetItemMeta as Repairable).repairCost } returns 1
        every { (sacrificeItemMeta as Repairable).repairCost } returns 3

        assertCalculateEnchantmentCost(enchantmentsToAdd, false, targetItemMeta, sacrificeItemMeta, 8)
    }

    @Test
    fun calculateEnchantmentCost_withSimpleEnchantment_reducedPrice() {
        // BINDING_CURSE        4
        // renaming             0
        // target penalty       0
        // sacrifice penalty    0
        // cost                 4

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.BINDING_CURSE, 1))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta =
            mockk(moreInterfaces = arrayOf(Repairable::class, EnchantmentStorageMeta::class))

        every { (targetItemMeta as Repairable).repairCost } returns 0
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, false, targetItemMeta, sacrificeItemMeta, 4)
    }

    @Test
    fun calculateEnchantmentCost_withMultipleEnchantments_reducedPrice_rename_penaltyOnTargetItem() {
        // BINDING_CURSE        4
        // MULTISHOT            4
        // renaming             1
        // target penalty       1
        // sacrifice penalty    0
        // cost                 10

        val enchantmentsToAdd = mapOf(
            Pair(Enchantment.BINDING_CURSE, 1),
            Pair(Enchantment.MULTISHOT, 2))

        val targetItemMeta: ItemMeta = mockk(moreInterfaces = arrayOf(Repairable::class))
        val sacrificeItemMeta: ItemMeta =
            mockk(moreInterfaces = arrayOf(Repairable::class, EnchantmentStorageMeta::class))

        every { (targetItemMeta as Repairable).repairCost } returns 1
        every { (sacrificeItemMeta as Repairable).repairCost } returns 0

        assertCalculateEnchantmentCost(enchantmentsToAdd, true, targetItemMeta, sacrificeItemMeta, 10)
    }

    private fun assertCalculateEnchantmentCost(
        enchantmentsToAdd: Map<Enchantment, Int>,
        isRenamed: Boolean,
        targetItemMeta: ItemMeta,
        sacrificeItemMeta: ItemMeta,
        expected: Int) {
        val actualEnchantmentCost = enchantmentCalculationHandler.calculateEnchantmentCost(
            enchantmentsToAdd,
            isRenamed,
            targetItemMeta,
            sacrificeItemMeta)

        Truth.assertThat(actualEnchantmentCost).isEqualTo(expected)
    }
}