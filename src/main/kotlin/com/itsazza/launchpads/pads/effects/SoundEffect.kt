package com.itsazza.launchpads.pads.effects

import org.bukkit.Sound
import org.bukkit.entity.Player

class SoundEffect(private val sound: Sound, private val volume: Float, private val pitch: Float) : Effect() {
    override fun play(player: Player) {
        player.playSound(player.location, sound, volume, pitch)
    }
}