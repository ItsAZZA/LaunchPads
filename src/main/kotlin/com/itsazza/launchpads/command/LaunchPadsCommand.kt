package com.itsazza.launchpads.command

import com.itsazza.launchpads.LaunchPads
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object LaunchPadsCommand : CommandExecutor {
    private val instance: LaunchPads = LaunchPads.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission("launchpads.reload")) {
            sender.sendMessage("§cNo permission: launchpads.reload")
            return false
        }
        if (args.isEmpty()) {
            sendUsage(sender)
            return false
        }
        return when (args[0].lowercase(Locale.getDefault())) {
            "reload" -> {
                instance.reload()
                sender.sendMessage("§6Reloaded config!")
                true
            }
            else -> {
                sendUsage(sender)
                true
            }
        }
    }

    private fun sendUsage(player: Player) {
        player.sendMessage(
            """
            §ePossible subcommands:
            §f- /launchpads reload : Reload the config
        """.trimIndent()
        )
    }
}