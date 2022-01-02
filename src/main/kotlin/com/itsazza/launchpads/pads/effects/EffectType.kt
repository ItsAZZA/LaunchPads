package com.itsazza.launchpads.pads.effects

import com.itsazza.launchpads.pads.effects.util.ExecutorType
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.potion.PotionEffectType

enum class EffectType(val dataPoints: Map<String, DataType>, val dataSize: Int = dataPoints.size) {
    COMMAND(mapOf("command" to DataType.STRING, "executor" to DataType.STRING)),
    MESSAGE(mapOf("text" to DataType.STRING)),
    PARTICLE(mapOf("type" to DataType.STRING, "amount" to DataType.INT, "delay" to DataType.INT)),
    POTION(mapOf("effect" to DataType.STRING, "duration" to DataType.INT, "amplifier" to DataType.INT, "particles" to DataType.BOOLEAN)),
    SOUND(mapOf("type" to DataType.STRING, "volume" to DataType.DOUBLE, "pitch" to DataType.DOUBLE));

    fun create(data: List<String>): Effect? {
        if (data.size != dataSize) return null

        return when (this) {
            COMMAND -> {
                val command = data[0]
                val executor = ExecutorType.values().firstOrNull { it.name == data[1].uppercase() } ?: return null
                CommandEffect(command, executor)
            }
            MESSAGE -> MessageEffect(data[0])
            PARTICLE -> {
                if (Bukkit.getVersion().contains("1.8")) return null
                val particle = Particle.values().firstOrNull { it.name == data[0].uppercase() } ?: return null
                val amount = data[1].toInt()
                val delay = data[2].toInt()
                ParticleEffect(particle, amount, delay)
            }
            POTION -> {
                val effect = PotionEffectType.values().firstOrNull { it.name == data[0].uppercase() } ?: return null
                val duration = data[1].toInt()
                val amplifier = data[2].toInt()
                val particles = data[3].toBoolean()
                PotionEffect(effect, duration, amplifier, particles)
            }
            SOUND -> {
                val sound = Sound.values().firstOrNull { it.name == data[0].uppercase() } ?: return null
                val volume = data[1].toFloat()
                val pitch = data[2].toFloat()
                SoundEffect(sound, volume, pitch)
            }
        }
    }

    enum class DataType {
        INT,
        STRING,
        BOOLEAN,
        DOUBLE
    }
}