package com.itsazza.launchpads.pads.effects

import com.itsazza.launchpads.pads.effects.util.ExecutorType
import org.bukkit.entity.Player

class CommandEffect(private val command: String, private val executor: ExecutorType) : Effect() {
    override fun play(player: Player) {
        val finalCommand = command.replace("<player>", player.name)
        when (executor) {
            ExecutorType.CONSOLE -> player.server.dispatchCommand(player.server.consoleSender, finalCommand)
            ExecutorType.PLAYER -> player.server.dispatchCommand(player, finalCommand)
        }
    }
}