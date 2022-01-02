package com.itsazza.launchpads.pads.effects

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

class PotionEffect(
    private val effectType: PotionEffectType,
    private val duration: Int,
    private val amplifier: Int,
    private val particles: Boolean
) : Effect() {
    override fun play(player: Player) {
        player.addPotionEffect(org.bukkit.potion.PotionEffect(effectType, duration * 20, amplifier, particles))
    }
}