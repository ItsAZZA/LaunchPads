package com.itsazza.launchpads.events

import com.itsazza.launchpads.cache.LaunchCache
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class OnPlayerTakeDamage : Listener {
    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity.type != EntityType.PLAYER) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        if (LaunchCache.check(event.entity.uniqueId)) return
        event.isCancelled = true
    }
}