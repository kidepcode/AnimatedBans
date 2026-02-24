package dev.kidepcode.animatedbans.animation

import org.bukkit.entity.Player
import java.util.UUID

class ActiveAnimation(
    val uuid: UUID,
    val playerName: String,
    val worldId: UUID,
    val baseX: Double,
    val baseY: Double,
    val baseZ: Double,
    val yaw: Float,
    val pitch: Float,
    val durationTicks: Int,
    val liftPerTick: Double,
    val titleEveryTicks: Int,
    val timeRaw: String,
    val reasonRaw: String,
    val initiatedBy: String
) {
    var tick: Int = 0

    private var savedWalkSpeed: Float = 0.2f
    private var savedFlySpeed: Float = 0.1f
    private var savedAllowFlight: Boolean = false
    private var savedFlying: Boolean = false
    private var savedGravity: Boolean = true
    private var savedInvulnerable: Boolean = false
    private var savedCollidable: Boolean = true

    fun applyLock(p: Player) {
        savedWalkSpeed = p.walkSpeed
        savedFlySpeed = p.flySpeed
        savedAllowFlight = p.allowFlight
        savedFlying = p.isFlying
        savedGravity = p.hasGravity()
        savedInvulnerable = p.isInvulnerable
        savedCollidable = p.isCollidable

        p.walkSpeed = 0f
        p.flySpeed = 0f
        p.allowFlight = true
        p.isFlying = true

        p.isInvulnerable = true
        p.isCollidable = false
        p.setGravity(false)

        p.velocity = p.velocity.zero()
        p.fallDistance = 0f
    }

    fun releaseLock(p: Player) {
        p.walkSpeed = savedWalkSpeed
        p.flySpeed = savedFlySpeed
        p.allowFlight = savedAllowFlight
        p.isFlying = savedFlying

        p.isInvulnerable = savedInvulnerable
        p.isCollidable = savedCollidable
        p.setGravity(savedGravity)

        p.velocity = p.velocity.zero()
        p.fallDistance = 0f
    }
}