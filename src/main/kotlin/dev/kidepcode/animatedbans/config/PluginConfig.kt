package dev.kidepcode.animatedbans.config

import org.bukkit.Particle

data class PluginConfig(
    val durationSeconds: Int,
    val liftTotalBlocks: Double,
    val titleIntervalSeconds: Int,

    val freezeLockYawPitch: Boolean,
    val freezeBlockCommands: Boolean,
    val freezeBlockInventory: Boolean,

    val particleType: Particle,
    val polygonPoints: Int,
    val cageRadius: Double,
    val radiusPulseAmplitude: Double,
    val radiusPulseSpeed: Double,
    val angularSpeed: Double,

    val columnHeight: Double,
    val columnParticlesPerColumn: Int,

    val ringRadiusOffset: Double,
    val ringParticles: Int,
    val ringHeightBottom: Double,
    val ringHeightTop: Double,

    val endExplosionEnabled: Boolean,
    val endExplosionParticle: Particle,
    val endExplosionCount: Int,
    val endExplosionOffset: Double,
    val endExplosionExtra: Double,
    val killTargetBeforeBan: Boolean,

    val title: String,
    val subtitle: String,

    val banCommandTemplate: String,

    val msgPrefix: String,
    val msgNoPermission: String,
    val msgUsage: String,
    val msgUsageReload: String,
    val msgPlayerNotFound: String,
    val msgAlreadyAnimating: String,
    val msgStarted: String,
    val msgReloaded: String,
)