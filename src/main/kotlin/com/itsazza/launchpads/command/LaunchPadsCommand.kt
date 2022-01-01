package com.itsazza.launchpads.command

import com.itsazza.launchpads.LaunchPads
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object LaunchPadsCommand : CommandExecutor {
    private val instance: LaunchPads = LaunchPads.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission("launchpads.command")) {
            sender.sendMessage("§cNo permission: launchpads.command")
            return false
        }
        if (args.isEmpty()) {
            sendUsage(sender)
            return false
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "reload" -> {
                instance.reloadConfig()
                sender.sendMessage("§6Reloaded config!")
                return true
            }
            "sound" -> {
                setSound(sender, args)
                return true
            }
            "toggle" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /launchpads toggle <sound|particle>")
                    return true
                }
                when (args[1].lowercase()) {
                    "sound" -> {
                        toggle(sender, "sound")
                        return true
                    }
                    "particle" -> {
                        toggle(sender, "particle")
                        return true
                    }
                    "iterations" -> {
                        run { toggle(sender, "iterations") }
                        sender.sendMessage("§cUsage: /launchpads toggle <sound|particle|iterations>")
                    }
                    else -> sender.sendMessage("§cUsage: /launchpads toggle <sound|particle|iterations>")
                }
                return true
            }
            "particle" -> {
                if (Bukkit.getServer().version.contains("1.8")) {
                    sender.sendMessage("§cParticles are not supported in 1.8")
                    return true
                }
                if (args.size < 3) {
                    sender.sendMessage("§cUsage: /launchpads particle <iterations|delay|amount> <amount>")
                    return true
                }
                when (val type = args[1].lowercase(Locale.getDefault())) {
                    "delay", "amount" -> {
                        val amount = args[2].toInt()
                        setParticleValue(sender, type, amount.toLong())
                        return true
                    }
                    "iterations" -> {
                        val amount = args[2].toInt()
                        setParticleValue(sender, "iterations.amount", amount.toLong())
                        return true
                    }
                    "set" -> {
                        setParticle(sender, args[2].uppercase(Locale.getDefault()))
                        return true
                    }
                    else -> sender.sendMessage("§cUsage: /launchpads particle <iterations|delay|amount|set> <value>")
                }
                return true
            }
            else -> {
                sendUsage(sender)
                return true
            }
        }
    }

    private fun setSound(player: Player, args: Array<String>) {
        if (args.size < 2) {
            player.sendMessage("§cUsage: /launchpads sound <sound> [volume=1.0] [pitch=1.0]")
            return
        }
        val sound = args[1].uppercase(Locale.getDefault())
        try {
            Sound.valueOf(sound)
        } catch (e: IllegalArgumentException) {
            return
        }
        var volume = 1.0
        var pitch = 1.0
        if (args.size == 4) {
            volume = args[2].toDouble()
            pitch = args[3].toDouble()
        }
        player.sendMessage("§eSet sound to $sound at volume $volume and pitch $pitch")
        val config = instance.config
        config.set("sound.sound", sound)
        config.set("sound.volume", volume)
        config.set("sound.pitch", pitch)
        instance.saveConfig()
    }

    private fun toggle(player: Player, type: String) {
        val path = "$type.enabled"
        val config = instance.config
        val value: Boolean = config.getBoolean(path)
        config.set(path, !value)
        instance.saveConfig()
        if (!value) player.sendMessage("§aEnabled $type") else player.sendMessage("§cDisabled $type")
    }

    private fun setParticleValue(player: Player, type: String, amount: Long) {
        val config = instance.config
        config.set("particle.$type", amount)
        instance.saveConfig()
        player.sendMessage("§eSet $type value to $amount")
    }

    private fun setParticle(player: Player, particle: String) {
        val bukkitParticle = Particle.values().firstOrNull { it.name.equals(particle, true) }
        if (bukkitParticle == null) {
            player.sendMessage("§cCouldn't find particle for $particle")
            return
        }

        instance.config.set("particle.particle", particle)
        instance.saveConfig()
        player.sendMessage("§eSet particle to $particle")
    }

    private fun sendUsage(player: Player) {
        player.sendMessage(
            """
            §ePossible subcommands:
            §f- /launchpads reload : Reload the config
            §f- /launchpads sound <sound> [<volume=1.0> <pitch=1.0>] : Change the sound
            §f- /launchpads particle <iterations|delay|amount|set> <value> : Change particle
            §f- /launchpads toggle <particle|sound|iterations> : Toggle stuff
        """.trimIndent()
        )
    }
}