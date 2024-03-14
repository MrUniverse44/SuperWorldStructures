package me.blueslime.superworldstructures.modules;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.logger.ConsoleLogger;
import me.blueslime.superworldstructures.modules.logger.PluginLogger;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class PluginModule implements PluginLogger {

    private final SuperWorldStructures plugin;

    public PluginModule(SuperWorldStructures plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();

    public abstract void shutdown();

    public abstract void reload();

    public PluginLogger getLogger() {
        return plugin.getModule(ConsoleLogger.class);
    }

    @Override
    public void info(String... messages) {
        getLogger().info(messages);
    }

    public <T extends PluginModule> T getModule(Class<T> module) {
        return plugin.getModule(module);
    }

    public Server getServer() {
        return plugin.getServer();
    }
    public FileConfiguration getSettings() {
        return plugin.getSettings();
    }

    public FileConfiguration getStructures() {
        return plugin.getStructures();
    }

    public boolean isDebug() {
        return getSettings().getBoolean("debug-mode", false);
    }

    public Collection<? extends Player> getOnlinePlayers() {
        return getServer().getOnlinePlayers();
    }

    public SuperWorldStructures getPlugin() {
        return plugin;
    }
}

