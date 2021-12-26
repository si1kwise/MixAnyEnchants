package main

import org.bukkit.plugin.java.JavaPlugin

class Loader : JavaPlugin() {

    private val eventRegistrationHandler = EventRegistrationHandler(this)

    override fun onEnable() {
        eventRegistrationHandler.initEventHandlers()
    }
}