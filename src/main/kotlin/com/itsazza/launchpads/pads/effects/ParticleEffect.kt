package com.itsazza.launchpads.pads.effects

import com.itsazza.launchpads.LaunchPads
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ParticleEffect(
    private val particle: Particle,
    private val amount: Int,
    private val delay: Int
) : Effect() {
    override fun play(player: Player) {
        if (Bukkit.getVersion().contains("1.8")) return // No particles for 1.8 ;(
        if (particle.dataType != Void::class.java) return // Particles with data are not supported

        object : BukkitRunnable() {
            override fun run() {
                val playerVelocity = player.velocity
                if (playerVelocity.y > 0) {
                    val location = player.location
                    location.world!!.spawnParticle(particle, location, amount, 0.0, 0.0, 0.0, 0.0)
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(LaunchPads.instance, 1L, delay.toLong())
    }
}