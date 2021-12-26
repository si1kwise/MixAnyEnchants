package business.enchantment

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import org.bukkit.enchantments.Enchantment
import org.junit.jupiter.api.Test

class EnchantmentConflictHandlerTest {

    private val enchantmentConflictHandler: EnchantmentConflictHandler = EnchantmentConflictHandler()

    @Test
    fun hasConflictingEnchantments_conflictsWith_mending() {
        val enchantment = mockk<Enchantment>()
        every { enchantment.conflictsWith(Enchantment.MENDING) } returns true

        val targetEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(Enchantment.MENDING, 1))
        val sacrificeEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(enchantment, 1))

        assertConflictingEnchantments(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap, true)
    }

    @Test
    fun hasConflictingEnchantments_noConflict() {
        val enchantment = mockk<Enchantment>()
        every { enchantment.conflictsWith(any()) } returns false

        val targetEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(Enchantment.THORNS, 1),
            Pair(Enchantment.LOYALTY, 1))
        val sacrificeEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(enchantment, 1))

        assertConflictingEnchantments(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap, false)
    }

    @Test
    fun hasConflictingEnchantments_conflictsWith_smite() {
        val enchantment = mockk<Enchantment>()
        every { enchantment.conflictsWith(any()) } returns false
        every { enchantment.conflictsWith(Enchantment.DAMAGE_UNDEAD) } returns true

        val targetEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(Enchantment.QUICK_CHARGE, 1),
            Pair(Enchantment.DAMAGE_UNDEAD, 1))
        val sacrificeEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(enchantment, 1)
        )

        assertConflictingEnchantments(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap, true)
    }

    @Test
    fun hasConflictingEnchantments_conflictsWith_protection_true_onSameItem() {
        val enchantment = mockk<Enchantment>()
        every { enchantment.conflictsWith(any()) } returns false

        val conflictingEnchantmentOnSameItem = mockk<Enchantment>()
        every { conflictingEnchantmentOnSameItem.conflictsWith(enchantment) } returns true

        val targetEnchantmentToLevelMap: Map<Enchantment, Int> = emptyMap()
        val sacrificeEnchantmentToLevelMap: Map<Enchantment, Int> = mapOf(
            Pair(enchantment, 1),
            Pair(conflictingEnchantmentOnSameItem, 1))

        assertConflictingEnchantments(targetEnchantmentToLevelMap, sacrificeEnchantmentToLevelMap, true)
    }

    @Test
    fun hasDuplicateEnchantment_sameEnchantment_sameLevel() {
        val enchantment = Enchantment.DAMAGE_ALL
        val level = 2

        val targetEnchantmentToLevelMap = mapOf(
            Pair(Enchantment.DAMAGE_ALL, 2))

        assertHasDuplicateEnchantment(enchantment, level, targetEnchantmentToLevelMap, true)
    }

    @Test
    fun hasDuplicateEnchantment_sameEnchantment_differentLevel() {
        val enchantment = Enchantment.DAMAGE_ALL
        val level = 3

        val targetEnchantmentToLevelMap = mapOf(
            Pair(Enchantment.DAMAGE_ALL, 1))

        assertHasDuplicateEnchantment(enchantment, level, targetEnchantmentToLevelMap, false)
    }

    @Test
    fun hasDuplicateEnchantment_noEnchantment() {
        val enchantment = Enchantment.DIG_SPEED
        val level = 3

        val targetEnchantmentToLevelMap = mapOf(
            Pair(Enchantment.DAMAGE_UNDEAD, 2),
            Pair(Enchantment.MENDING, 1))

        assertHasDuplicateEnchantment(enchantment, level, targetEnchantmentToLevelMap, false)
    }

    private fun assertHasDuplicateEnchantment(
        enchantment: Enchantment,
        level: Int,
        targetEnchantmentToLevelMap: Map<Enchantment, Int>,
        expected: Boolean) {
        val hasDuplicateEnchantment = enchantmentConflictHandler.hasDuplicateEnchantment(
            enchantment,
            level,
            targetEnchantmentToLevelMap)

        Truth.assertThat(hasDuplicateEnchantment).isEqualTo(expected)
    }

    private fun assertConflictingEnchantments(
        targetEnchantmentToLevelMap: Map<Enchantment, Int>,
        sacrificeEnchantmentToLevelMap: Map<Enchantment, Int>,
        expected: Boolean) {
        val hasConflictingEnchantments = enchantmentConflictHandler.hasConflictingEnchantments(
            targetEnchantmentToLevelMap,
            sacrificeEnchantmentToLevelMap)

        Truth
            .assertThat(hasConflictingEnchantments)
            .isEqualTo(expected)
    }
}