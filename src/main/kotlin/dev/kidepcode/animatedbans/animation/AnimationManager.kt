package dev.kidepcode.animatedbans.animation

import dev.kidepcode.animatedbans.config.ConfigManager
import dev.kidepcode.animatedbans.config.PluginConfig
import dev.kidepcode.animatedbans.util.Mini
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.cos
import kotlin.math.sin

class AnimationManager(
    private val plugin: JavaPlugin,
    private val configManager: ConfigManager
) {
    private val active = ConcurrentHashMap<UUID, ActiveAnimation>()
    private var ticker: BukkitTask? = null

    fun isAnimating(uuid: UUID): Boolean = active.containsKey(uuid)
    fun isFrozen(uuid: UUID): Boolean = active.containsKey(uuid)

    fun start(target: Player, timeRaw: String, reasonRaw: String, initiatedBy: String) {
        val cfg = configManager.get()

        val durationTicks = (cfg.durationSeconds * 20).coerceAtLeast(1)
        val liftPerTick = cfg.liftTotalBlocks / durationTicks.toDouble()
        val titleEveryTicks = (cfg.titleIntervalSeconds * 20).coerceAtLeast(1)

        val base = target.location
        val anim = ActiveAnimation(
            uuid = target.uniqueId,
            playerName = target.name,
            worldId = target.world.uid,
            baseX = base.x,
            baseY = base.y,
            baseZ = base.z,
            yaw = base.yaw,
            pitch = base.pitch,
            durationTicks = durationTicks,
            liftPerTick = liftPerTick,
            titleEveryTicks = titleEveryTicks,
            timeRaw = timeRaw,
            reasonRaw = reasonRaw,
            initiatedBy = initiatedBy
        )

        if (active.putIfAbsent(target.uniqueId, anim) != null) return

        anim.applyLock(target)
        ensureTicker()
    }

    fun shutdown() {
        ticker?.cancel()
        ticker = null
        for ((_, anim) in active) Bukkit.getPlayer(anim.uuid)?.let { anim.releaseLock(it) }
        active.clear()
    }

    private fun ensureTicker() {
        if (ticker != null) return
        ticker = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { tick() }, 1L, 1L)
    }

    private fun tick() {
        if (active.isEmpty()) {
            ticker?.cancel()
            ticker = null
            return
        }

        val cfg = configManager.get()
        val it = active.entries.iterator()
        while (it.hasNext()) {
            val anim = it.next().value
            val p = Bukkit.getPlayer(anim.uuid)

            anim.tick++

            if (p != null && p.isOnline) {
                tickPlayer(cfg, p, anim)
            }

            if (anim.tick >= anim.durationTicks) {
                if (p != null && p.isOnline) {
                    // снять логику "бессмертия" перед kill/баном
                    anim.releaseLock(p)

                    if (cfg.endExplosionEnabled) {
                        playEndExplosion(cfg, p)
                    }

                    if (cfg.killTargetBeforeBan) {
                        killTarget(p)
                    }
                }

                runBan(cfg.banCommandTemplate, anim.playerName, anim.timeRaw, anim.reasonRaw)
                it.remove()
            }
        }
    }

    private fun tickPlayer(cfg: PluginConfig, p: Player, anim: ActiveAnimation) {
        p.fallDistance = 0f
        p.velocity = p.velocity.zero()

        val y = anim.baseY + anim.tick * anim.liftPerTick
        p.teleportAsync(reuseLocation(p.location, anim.baseX, y, anim.baseZ, anim.yaw, anim.pitch))

        // Title from the start
        if (anim.tick == 1 || (anim.titleEveryTicks > 0 && anim.tick % anim.titleEveryTicks == 0)) {
            val resolver = TagResolver.resolver(
                Placeholder.parsed("player", anim.playerName),
                Placeholder.parsed("time", anim.timeRaw),
                Placeholder.parsed("reason", anim.reasonRaw)
            )
            val title = Mini.parse(cfg.title, resolver)
            val subtitle = Mini.parse(cfg.subtitle, resolver)

            p.showTitle(
                Title.title(
                    title,
                    subtitle,
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofMillis(250))
                )
            )
        }

        spawnRuneCage(cfg, p, anim)
    }

    private fun spawnRuneCage(cfg: PluginConfig, p: Player, anim: ActiveAnimation) {
        val world = p.world

        val baseX = anim.baseX
        val baseY = anim.baseY + anim.tick * anim.liftPerTick
        val baseZ = anim.baseZ

        val points = cfg.polygonPoints
        val t = anim.tick.toDouble()

        val radius = cfg.cageRadius + sin(t * cfg.radiusPulseSpeed) * cfg.radiusPulseAmplitude
        val phase = t * cfg.angularSpeed

        val vx = DoubleArray(points)
        val vz = DoubleArray(points)
        for (i in 0 until points) {
            val a = phase + i * (Math.PI * 2.0 / points.toDouble())
            vx[i] = baseX + cos(a) * radius
            vz[i] = baseZ + sin(a) * radius
        }

        val colCount = cfg.columnParticlesPerColumn
        if (colCount > 0 && cfg.columnHeight > 0.0) {
            val h = cfg.columnHeight
            val step = h / (colCount + 1).toDouble()
            for (i in 0 until points) {
                val x = vx[i]
                val z = vz[i]
                for (k in 1..colCount) {
                    val y = baseY + k * step
                    world.spawnParticle(cfg.particleType, x, y, z, 1, 0.0, 0.0, 0.0, 0.0)
                }
            }
        }

        val ringCount = cfg.ringParticles
        if (ringCount > 0) {
            val ringR = (radius + cfg.ringRadiusOffset).coerceAtLeast(0.0)
            spawnRing(world, cfg.particleType, baseX, baseY + cfg.ringHeightBottom, baseZ, ringR, ringCount, phase * 1.35)
            spawnRing(world, cfg.particleType, baseX, baseY + cfg.ringHeightTop, baseZ, ringR, ringCount, -phase * 1.10)
        }

        val edgeIndex = (anim.tick / 2) % points
        val next = (edgeIndex + 1) % points
        val mix = (anim.tick % 2) * 0.5
        val sx = vx[edgeIndex] + (vx[next] - vx[edgeIndex]) * mix
        val sz = vz[edgeIndex] + (vz[next] - vz[edgeIndex]) * mix
        world.spawnParticle(cfg.particleType, sx, baseY + 1.35, sz, 1, 0.0, 0.0, 0.0, 0.0)
    }

    private fun spawnRing(
        world: org.bukkit.World,
        particle: org.bukkit.Particle,
        cx: Double,
        cy: Double,
        cz: Double,
        r: Double,
        count: Int,
        phase: Double
    ) {
        for (i in 0 until count) {
            val a = phase + i * (Math.PI * 2.0 / count.toDouble())
            val x = cx + cos(a) * r
            val z = cz + sin(a) * r
            world.spawnParticle(particle, x, cy, z, 1, 0.0, 0.0, 0.0, 0.0)
        }
    }

    private fun playEndExplosion(cfg: PluginConfig, p: Player) {
        val loc = p.location
        val w = loc.world ?: return

        val c = cfg.endExplosionCount
        if (c <= 0) return

        w.spawnParticle(
            cfg.endExplosionParticle,
            loc.x, loc.y + 1.0, loc.z,
            c,
            cfg.endExplosionOffset, cfg.endExplosionOffset, cfg.endExplosionOffset,
            cfg.endExplosionExtra
        )
    }

    private fun killTarget(p: Player) {
        if (p.isDead) return
        // Safe direct kill (no violence details, just mechanics)
        p.health = 0.0
    }

    private fun runBan(template: String, player: String, time: String, reason: String) {
        val cmd = template
            .replace("{player}", player, ignoreCase = false)
            .replace("{time}", time, ignoreCase = false)
            .replace("{reason}", reason, ignoreCase = false)

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
    }

    private fun reuseLocation(base: Location, x: Double, y: Double, z: Double, yaw: Float, pitch: Float): Location {
        base.x = x
        base.y = y
        base.z = z
        base.yaw = yaw
        base.pitch = pitch
        return base
    }
}