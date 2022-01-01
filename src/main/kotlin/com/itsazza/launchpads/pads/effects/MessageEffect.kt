package com.itsazza.launchpads.pads.effects

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class MessageEffect(private val message: String) : Effect() {
    override fun play(player: Player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }
}