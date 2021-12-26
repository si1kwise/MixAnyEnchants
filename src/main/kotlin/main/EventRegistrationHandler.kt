package main

import business.enchantment.EnchantmentCalculationHandler
import business.enchantment.EnchantmentConflictHandler
import main.eventhandler.PrepareAnvilEventHandler
import org.bukkit.plugin.java.JavaPlugin

class EventRegistrationHandler(
    private val javaPlugin: JavaPlugin) {

    private val enchantmentCalculationHandler = EnchantmentCalculationHandler()
    private val enchantmentConflictHandler = EnchantmentConflictHandler()

    fun initEventHandlers() {
        val prepareAnvilEventHandler = PrepareAnvilEventHandler(
            javaPlugin,
            enchantmentCalculationHandler,
            enchantmentConflictHandler)

        javaPlugin.server.pluginManager.registerEvents(prepareAnvilEventHandler, javaPlugin)
    }
}