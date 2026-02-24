package dev.kidepcode.animatedbans.command

import dev.kidepcode.animatedbans.animation.AnimationManager
import dev.kidepcode.animatedbans.config.ConfigManager
import dev.kidepcode.animatedbans.util.Mini
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class AnimatedBansCommand(
    private val configManager: ConfigManager,
    private val animationManager: AnimationManager
) : org.bukkit.command.CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val cfg = configManager.get()

        fun send(raw: String, resolver: TagResolver = TagResolver.empty()) {
            sender.sendMessage(Mini.parse(cfg.msgPrefix + raw, resolver))
        }

        if (args.isEmpty()) {
            send(cfg.msgUsage)
            return true
        }

        val sub = args[0]
        if (equalsIgnoreCase(sub, "reload")) {
            if (!sender.hasPermission("animatedbans.reload")) {
                send(cfg.msgNoPermission)
                return true
            }
            if (args.size != 1) {
                send(cfg.msgUsageReload)
                return true
            }
            configManager.reload()
            val nc = configManager.get()
            sender.sendMessage(Mini.parse(nc.msgPrefix + nc.msgReloaded))
            return true
        }

        if (!equalsIgnoreCase(sub, "ban")) {
            send(cfg.msgUsage)
            return true
        }

        if (!sender.hasPermission("animatedbans.ban")) {
            send(cfg.msgNoPermission)
            return true
        }

        if (args.size < 4) {
            send(cfg.msgUsage)
            return true
        }

        val targetName = args[1]
        val time = args[2]

        val reason = joinReason(args, 3)
        if (reason.isEmpty()) {
            send(cfg.msgUsage)
            return true
        }

        val target = Bukkit.getPlayerExact(targetName)
        if (target == null) {
            val r = TagResolver.resolver(Placeholder.parsed("player", targetName))
            send(cfg.msgPlayerNotFound, r)
            return true
        }

        if (animationManager.isAnimating(target.uniqueId)) {
            send(cfg.msgAlreadyAnimating)
            return true
        }

        animationManager.start(
            target = target,
            timeRaw = time,
            reasonRaw = reason,
            initiatedBy = sender.name
        )

        val r = TagResolver.resolver(Placeholder.parsed("player", target.name))
        send(cfg.msgStarted, r)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val out = ArrayList<String>(16)

        when (args.size) {
            1 -> {
                val p = args[0]
                if (sender.hasPermission("animatedbans.ban") && startsWithIgnoreCase("ban", p)) out.add("ban")
                if (sender.hasPermission("animatedbans.reload") && startsWithIgnoreCase("reload", p)) out.add("reload")
                return out
            }
            2 -> {
                if (!sender.hasPermission("animatedbans.ban")) return out
                if (!equalsIgnoreCase(args[0], "ban")) return out

                val prefix = args[1]
                for (pl in Bukkit.getOnlinePlayers()) {
                    val name = pl.name
                    if (startsWithIgnoreCase(name, prefix)) out.add(name)
                }
                return out
            }
            3 -> {
                if (!sender.hasPermission("animatedbans.ban")) return out
                if (!equalsIgnoreCase(args[0], "ban")) return out

                val prefix = args[2]
                val times = TIME_SUGGESTIONS
                for (i in times.indices) {
                    val s = times[i]
                    if (startsWithIgnoreCase(s, prefix)) out.add(s)
                }
                return out
            }
            else -> {
                if (!sender.hasPermission("animatedbans.ban")) return out
                if (!equalsIgnoreCase(args[0], "ban")) return out

                val prefix = args[3]
                val reasons = REASON_SUGGESTIONS
                for (i in reasons.indices) {
                    val s = reasons[i]
                    if (startsWithIgnoreCase(s, prefix)) out.add(s)
                }
                return out
            }
        }
    }

    companion object {
        private val TIME_SUGGESTIONS = arrayOf(
            "10m", "30m", "1h", "6h", "12h", "1d", "3d", "7d", "30d"
        )

        private val REASON_SUGGESTIONS = arrayOf(
            "Cheating", "Xray", "KillAura", "MultiAccount", "Griefing", "Abuse"
        )

        fun warmUp() {
            // Прогрев Kotlin stdlib/строковых операций, чтобы пик не приходился на первый /tab
            TIME_SUGGESTIONS.size
            REASON_SUGGESTIONS.size
            equalsIgnoreCase("ban", "BAN")
            startsWithIgnoreCase("animatedbans", "a")
            joinReason(arrayOf("ban", "p", "10m", "test"), 3)
        }

        private fun equalsIgnoreCase(a: String, b: String): Boolean {
            return a.equals(b, ignoreCase = true)
        }

        private fun startsWithIgnoreCase(full: String, prefix: String): Boolean {
            if (prefix.isEmpty()) return true
            if (prefix.length > full.length) return false
            return full.regionMatches(0, prefix, 0, prefix.length, ignoreCase = true)
        }

        private fun joinReason(args: Array<out String>, start: Int): String {
            val n = args.size
            if (start >= n) return ""
            if (start == n - 1) return args[start]

            var len = 0
            for (i in start until n) len += args[i].length
            len += (n - start - 1)

            val sb = StringBuilder(len)
            sb.append(args[start])
            for (i in start + 1 until n) {
                sb.append(' ')
                sb.append(args[i])
            }
            return sb.toString()
        }
    }
}