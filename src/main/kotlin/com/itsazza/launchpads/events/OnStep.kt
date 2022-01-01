package com.itsazza.launchpads.events

import com.itsazza.launchpads.LaunchPads
import com.itsazza.launchpads.cache.LaunchCache
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil

class OnStep : Listener {
    private val instance = LaunchPads.instance
    private val config = instance.config

    @EventHandler
    fun onStep(event: PlayerInteractEvent) {
        if (event.action != Action.PHYSICAL) return

        val block = event.clickedBlock ?: return
        if (!block.type.name.contains("_PLATE")) return

        val signLocation = config.getInt("signYOffset", -2)
        val dataBlock = block.getRelative(0, signLocation, 0)
        val sign = dataBlock.state as? Sign ?: return
        val label = sign.getLine(0)
        if (!label.equals("[launch]", true)) return

        val player = event.player
        val x = sign.getLine(1).toDoubleOrNull()
        val y = sign.getLine(2).toDoubleOrNull()
        val z = sign.getLine(3).toDoubleOrNull()

        if (x == null || y == null || z == null) {
            player.sendMessage("§cError! Please check the number values on lines 2-4")
            return
        }

        val soundsEnabled = config.getBoolean("sound.enabled")
        if (soundsEnabled) {
            val sound = config.getString("sound.sound")
            val bukkitSound = Sound.values().firstOrNull { it.name == sound }
            if (bukkitSound == null) {
                player.sendMessage("§cError: Could not find sound for $sound")
                return
            }

            val volume = config.getDouble("sound.volume").toFloat()
            val pitch = config.getDouble("sound.pitch").toFloat()

            player.playSound(player.location, bukkitSound, volume, pitch)
        }

        val velocity = player.velocity
        velocity.x = x
        velocity.y = y
        velocity.z = z

        // Puts the player into the launch cache
        if (config.getBoolean("falldamage.prevent") && player.gameMode != GameMode.CREATIVE) {
            LaunchCache.put(player.uniqueId, ceil(config.getDouble("falldamage.multiplier") * velocity.y * 1000).toLong())
        }

        // Fixes a problem pre-1.13 not launching players upwards...
        object : BukkitRunnable() {
            override fun run() {
                player.velocity = velocity
            }
        }.runTaskLater(instance, 1L)

        if (config.getBoolean("particle.enabled")) {
            if (Bukkit.getVersion().contains("1.8")) return

            val particleString = config.getString("particle.particle")
            val amount = config.getInt("particle.amount")
            val particle = Particle.values().firstOrNull { it.name == particleString }
            if (particle == null) {
                player.sendMessage("§cError: Could not find particle for $particleString")
                return
            }

            // If the particle requires data, just return for now (future feature?)
            if (particle.dataType != Void::class.java) return
            val delay = config.getLong("particle.delay")

            if (config.getBoolean("particle.iterations.enabled")) {
                object : BukkitRunnable() {
                    private var i = 0
                    private val iterations = config.getInt("particle.iterations.amount")
                    override fun run() {
                        i++
                        val location = player.location
                        location.world!!.spawnParticle(particle, location, amount)
                        if (i >= iterations) {
                            cancel()
                        }
                    }
                }.runTaskTimer(instance, 1L, delay)
            } else {
                object : BukkitRunnable() {
                    override fun run() {
                        val playerVelocity = player.velocity
                        if (playerVelocity.y > 0) {
                            val location = player.location
                            location.world!!.spawnParticle(particle, location, amount)
                        } else {
                            cancel()
                        }
                    }
                }.runTaskTimer(instance, 1L, delay)
            }
        }
    }
}