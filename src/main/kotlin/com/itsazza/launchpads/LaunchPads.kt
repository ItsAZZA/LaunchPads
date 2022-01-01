package com.itsazza.launchpads

import com.itsazza.launchpads.command.LaunchPadsCommand
import com.itsazza.launchpads.events.OnPlayerTakeDamage
import com.itsazza.launchpads.events.OnSignPlace
import com.itsazza.launchpads.events.OnStep
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class LaunchPads : JavaPlugin() {
    companion object {
        lateinit var instance: LaunchPads
            private set
    }

    override fun onEnable() {
        this.saveDefaultConfig()
        instance = this
        getCommand("launchpads")?.setExecutor(LaunchPadsCommand)
        Bukkit.getServer().pluginManager.registerEvents(OnSignPlace(), this)
        Bukkit.getServer().pluginManager.registerEvents(OnStep(), this)
        Bukkit.getServer().pluginManager.registerEvents(OnPlayerTakeDamage(), this)
        Metrics(this, 10774)
    }
}