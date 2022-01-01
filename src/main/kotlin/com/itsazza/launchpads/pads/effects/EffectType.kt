package com.itsazza.launchpads.pads.effects

import org.bukkit.Particle
import org.bukkit.Sound

enum class EffectType(val dataPoints: List<String>, private val dataSize: Int = dataPoints.size) {
    MESSAGE(listOf("text")),
    PARTICLE(listOf("type", "amount", "delay")),
    SOUND(listOf("type", "volume", "pitch"));

    fun create(data: List<String>) : Effect? {
        if (data.size != dataSize) return null

        return when (this) {
            MESSAGE -> MessageEffect(data[0])
            PARTICLE -> {
                val particle = Particle.values().firstOrNull{ it.name == data[0].uppercase() } ?: return null
                val amount = data[1].toInt()
                val delay = data[2].toInt()
                ParticleEffect(particle, amount, delay)
            }
            SOUND -> {
                val sound = Sound.values().firstOrNull{ it.name == data[0].uppercase() } ?: return null
                val volume = data[1].toFloat()
                val pitch = data[2].toFloat()
                SoundEffect(sound, volume, pitch)
            }
        }
    }
}