package com.itsazza.launchpads.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.*
import java.util.concurrent.TimeUnit

object LaunchCache {
    private val launches: Cache<UUID, Long> = CacheBuilder.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build()

    fun put(uuid: UUID, time: Long) {
        launches.put(uuid, System.currentTimeMillis() + time)
    }

    fun check(uuid: UUID): Boolean {
        val value = launches.getIfPresent(uuid) ?: return true
        if (value > System.currentTimeMillis()) return false
        launches.invalidate(uuid)
        return true
    }
}