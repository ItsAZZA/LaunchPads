package com.itsazza.launchpads.pads

import com.itsazza.launchpads.LaunchPads
import com.itsazza.launchpads.pads.effects.Effect
import com.itsazza.launchpads.pads.effects.EffectType
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.system.measureTimeMillis

object LaunchPadStorage {
    private val launchPads = mutableMapOf<String, LaunchPad>()
    private val instance = LaunchPads.instance

    fun load() {
        launchPads.clear()
        measureTimeMillis {
            val launchPadFile = File(instance.dataFolder, "launchpads.yml")
            if (!launchPadFile.exists()) instance.saveResource("launchpads.yml", true)

            val config = YamlConfiguration.loadConfiguration(launchPadFile)
            val keys = config.getKeys(false)
            for (key in keys) {
                val padType = config.getString("$key.type") ?: continue
                val launchdPadType = LaunchPadType.values().firstOrNull { it.name == padType } ?: continue
                val section = config.getConfigurationSection("$key.effects")
                if (section == null) {
                    launchPads[key] = LaunchPad(launchdPadType, null)
                    continue
                }

                val effects = mutableListOf<Effect>()
                val effectKeys = section.getKeys(false)

                for (effectKey in effectKeys) {
                    val effectType = EffectType.values().firstOrNull { it.name == effectKey.uppercase() } ?: continue
                    val data = mutableListOf<String>()
                    for (point in effectType.dataPoints.keys) {
                        data += section.getString("$effectKey.$point") ?: continue
                    }
                    val created = effectType.create(data)
                    if (created == null) {
                        Bukkit.getLogger().warning(
                            "Failed to create effect \"$effectKey\" for launchpad $key. This type needs the following values in config: ${
                                effectType.dataPoints.map { "${it.key} (${it.value.toString().lowercase()})" }.joinToString(", ") }"
                        )
                        continue
                    }
                    effects += created
                }

                launchPads[key] = LaunchPad(launchdPadType, effects)
            }
        }.also {
            Bukkit.getLogger()
                .info("[LaunchPads] Loaded ${launchPads.size} LaunchPad ${if (launchPads.size > 1) "types" else "type"} in $it ms.")
        }
    }

    fun get(name: String): LaunchPad? = launchPads[name]
}