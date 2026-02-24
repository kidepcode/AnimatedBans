package dev.kidepcode.animatedbans

import dev.kidepcode.animatedbans.animation.AnimationManager
import dev.kidepcode.animatedbans.command.AnimatedBansCommand
import dev.kidepcode.animatedbans.config.ConfigManager
import dev.kidepcode.animatedbans.listener.FreezeListener
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class AnimatedBansPlugin : JavaPlugin() {

    lateinit var configManager: ConfigManager
        private set

    lateinit var animationManager: AnimationManager
        private set

    override fun onEnable() {
        saveDefaultConfig()

        configManager = ConfigManager(this)
        animationManager = AnimationManager(this, configManager)

        server.pluginManager.registerEvents(FreezeListener(animationManager, configManager), this)

        val handler = AnimatedBansCommand(configManager, animationManager)

        AnimatedBansCommand.warmUp()

        val main: PluginCommand = requireNotNull(getCommand("animatedbans"))
        main.setExecutor(handler)
        main.tabCompleter = handler
    }

    override fun onDisable() {
        animationManager.shutdown()
    }
}