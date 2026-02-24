package dev.kidepcode.animatedbans.listener

import dev.kidepcode.animatedbans.animation.AnimationManager
import dev.kidepcode.animatedbans.config.ConfigManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.*

class FreezeListener(
    private val animationManager: AnimationManager,
    private val configManager: ConfigManager
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMove(e: PlayerMoveEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        val cfg = configManager.get()

        val from = e.from
        val to = e.to ?: return

        val moved = from.x != to.x || from.y != to.y || from.z != to.z
        if (!moved && !cfg.freezeLockYawPitch) return

        if (cfg.freezeLockYawPitch) {
            to.yaw = from.yaw
            to.pitch = from.pitch
        }

        to.x = from.x
        to.y = from.y
        to.z = from.z
        e.to = to
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onTeleport(e: PlayerTeleportEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        if (e.cause == PlayerTeleportEvent.TeleportCause.PLUGIN) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInteract(e: PlayerInteractEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInteractEntity(e: PlayerInteractEntityEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPickup(e: PlayerAttemptPickupItemEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onSwap(e: PlayerSwapHandItemsEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        if (!configManager.get().freezeBlockCommands) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as? org.bukkit.entity.Player ?: return
        if (!animationManager.isFrozen(p.uniqueId)) return
        if (!configManager.get().freezeBlockInventory) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryDrag(e: InventoryDragEvent) {
        val p = e.whoClicked as? org.bukkit.entity.Player ?: return
        if (!animationManager.isFrozen(p.uniqueId)) return
        if (!configManager.get().freezeBlockInventory) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(e: BlockBreakEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlace(e: BlockPlaceEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDamage(e: EntityDamageEvent) {
        val p = e.entity as? org.bukkit.entity.Player ?: return
        if (!animationManager.isFrozen(p.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        val damager = e.damager as? org.bukkit.entity.Player ?: return
        if (!animationManager.isFrozen(damager.uniqueId)) return
        e.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onAnimation(e: PlayerAnimationEvent) {
        if (!animationManager.isFrozen(e.player.uniqueId)) return
        e.isCancelled = true
    }
}