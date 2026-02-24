package dev.kidepcode.animatedbans.config

import org.bukkit.Particle
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {

    @Volatile
    private var cached: PluginConfig = load(plugin.config)

    fun get(): PluginConfig = cached

    fun reload() {
        plugin.reloadConfig()
        cached = load(plugin.config)
    }

    private fun load(cfg: FileConfiguration): PluginConfig {
        fun particle(path: String, def: Particle): Particle {
            val raw = cfg.getString(path) ?: return def
            return runCatching { Particle.valueOf(raw.uppercase()) }.getOrElse { def }
        }

        val durationSeconds = cfg.getInt("animation.durationSeconds").coerceAtLeast(1)
        val liftTotalBlocks = cfg.getDouble("animation.liftTotalBlocks")
        val titleIntervalSeconds = cfg.getInt("animation.titleIntervalSeconds").coerceAtLeast(1)

        val freezeLockYawPitch = cfg.getBoolean("animation.freeze.lockYawPitch")
        val freezeBlockCommands = cfg.getBoolean("animation.freeze.blockCommands")
        val freezeBlockInventory = cfg.getBoolean("animation.freeze.blockInventory")

        val particleType = particle("animation.particles.type", Particle.ENCHANT)
        val polygonPoints = cfg.getInt("animation.particles.polygonPoints").coerceIn(3, 12)
        val cageRadius = cfg.getDouble("animation.particles.cageRadius").coerceAtLeast(0.0)
        val radiusPulseAmplitude = cfg.getDouble("animation.particles.radiusPulseAmplitude").coerceAtLeast(0.0)
        val radiusPulseSpeed = cfg.getDouble("animation.particles.radiusPulseSpeed")
        val angularSpeed = cfg.getDouble("animation.particles.angularSpeed")

        val columnHeight = cfg.getDouble("animation.particles.columnHeight").coerceAtLeast(0.0)
        val columnParticlesPerColumn = cfg.getInt("animation.particles.columnParticlesPerColumn").coerceIn(0, 10)

        val ringRadiusOffset = cfg.getDouble("animation.particles.ringRadiusOffset")
        val ringParticles = cfg.getInt("animation.particles.ringParticles").coerceIn(0, 40)
        val ringHeightBottom = cfg.getDouble("animation.particles.ringHeightBottom")
        val ringHeightTop = cfg.getDouble("animation.particles.ringHeightTop")

        val endExplosionEnabled = cfg.getBoolean("animation.ending.explosion.enabled")
        val endExplosionParticle = particle("animation.ending.explosion.particle", Particle.EXPLOSION)
        val endExplosionCount = cfg.getInt("animation.ending.explosion.count").coerceIn(0, 50)
        val endExplosionOffset = cfg.getDouble("animation.ending.explosion.offset").coerceAtLeast(0.0)
        val endExplosionExtra = cfg.getDouble("animation.ending.explosion.extra")
        val killTargetBeforeBan = cfg.getBoolean("animation.ending.killTargetBeforeBan")

        val title = cfg.getString("titles.title") ?: "<red><bold>CHEATER FOUND</bold>"
        val subtitle = cfg.getString("titles.subtitle") ?: "<gray><player> <dark_gray>• <yellow><time> <dark_gray>• <white><reason>"

        val banCommandTemplate = cfg.getString("banCommand.template") ?: "tempban {player} {time} {reason}"

        val msgPrefix = cfg.getString("messages.prefix") ?: "<dark_gray>[<red>AnimatedBans</red>]</dark_gray> "
        val msgNoPermission = cfg.getString("messages.noPermission") ?: "<red>You don't have permission to do that."
        val msgUsage = cfg.getString("messages.usage") ?: "<gray>Usage: <yellow>/animatedbans ban <player> <time> <reason...>"
        val msgUsageReload = cfg.getString("messages.usageReload") ?: "<gray>Usage: <yellow>/animatedbans reload"
        val msgPlayerNotFound = cfg.getString("messages.playerNotFound") ?: "<red>Player not found: <yellow><player>"
        val msgAlreadyAnimating = cfg.getString("messages.alreadyAnimating") ?: "<red>This player is already in an animation."
        val msgStarted = cfg.getString("messages.started") ?: "<green>Animation started for <yellow><player><green>."
        val msgReloaded = cfg.getString("messages.reloaded") ?: "<green>Configuration reloaded."

        return PluginConfig(
            durationSeconds = durationSeconds,
            liftTotalBlocks = liftTotalBlocks,
            titleIntervalSeconds = titleIntervalSeconds,

            freezeLockYawPitch = freezeLockYawPitch,
            freezeBlockCommands = freezeBlockCommands,
            freezeBlockInventory = freezeBlockInventory,

            particleType = particleType,
            polygonPoints = polygonPoints,
            cageRadius = cageRadius,
            radiusPulseAmplitude = radiusPulseAmplitude,
            radiusPulseSpeed = radiusPulseSpeed,
            angularSpeed = angularSpeed,

            columnHeight = columnHeight,
            columnParticlesPerColumn = columnParticlesPerColumn,

            ringRadiusOffset = ringRadiusOffset,
            ringParticles = ringParticles,
            ringHeightBottom = ringHeightBottom,
            ringHeightTop = ringHeightTop,

            endExplosionEnabled = endExplosionEnabled,
            endExplosionParticle = endExplosionParticle,
            endExplosionCount = endExplosionCount,
            endExplosionOffset = endExplosionOffset,
            endExplosionExtra = endExplosionExtra,
            killTargetBeforeBan = killTargetBeforeBan,

            title = title,
            subtitle = subtitle,

            banCommandTemplate = banCommandTemplate,

            msgPrefix = msgPrefix,
            msgNoPermission = msgNoPermission,
            msgUsage = msgUsage,
            msgUsageReload = msgUsageReload,
            msgPlayerNotFound = msgPlayerNotFound,
            msgAlreadyAnimating = msgAlreadyAnimating,
            msgStarted = msgStarted,
            msgReloaded = msgReloaded
        )
    }
}