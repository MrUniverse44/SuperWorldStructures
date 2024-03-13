package me.blueslime.superworldstructures.modules.plugin;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.logger.ConsoleLogger;
import me.blueslime.superworldstructures.modules.utils.file.FileUtilities;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Plugin extends JavaPlugin {
    private final Map<Class<?>, Listener> listenerMap = new HashMap<>();
    private final Map<Class<?>, PluginModule> moduleMap = new HashMap<>();
    private FileConfiguration structures;
    private FileConfiguration settings;

    private String serverVersion;

    protected void initialize(SuperWorldStructures plugin) {
        build();

        String version = plugin.getServer().getClass().getPackage().getName()
                .replace(".", ",")
                .split(",")[3];

        serverVersion = version.substring(0, version.lastIndexOf("_"));

        registerModules();

        loadModules();
    }

    public void refreshStructures() {
        structures = loadConfiguration(getDataFolder(), "structures.yml");
    }

    public void build() {
        structures = loadConfiguration(getDataFolder(), "structures.yml");
        settings = loadConfiguration(getDataFolder(), "settings.yml");

        File folder = new File(getDataFolder(), "schematics");

        File worlds = new File(getDataFolder(), "worlds");

        if (!folder.exists() && folder.mkdirs()) {
            getLogger().info("Schematics folder has been created.");
        }

        if (!worlds.exists() && worlds.mkdirs()) {
            FileUtilities.saveResource(
                new File(worlds, "world.yml"), Plugin.class.getResourceAsStream("/world-template.yml")
            );
            getLogger().info("World folder created");
        }
    }

    public abstract void registerModules();

    private void loadModules() {
        for (PluginModule module : moduleMap.values()) {
            module.initialize();
        }
    }

    public void shutdown() {
        for (PluginModule module : moduleMap.values()) {
            module.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PluginModule> T getModule(Class<T> module) {
        return (T) moduleMap.get(module);
    }

    public Plugin registerModule(PluginModule... modules) {
        if (modules != null && modules.length >= 1) {
            for (PluginModule module : modules) {
                moduleMap.put(module.getClass(), module);
            }
        }
        return this;
    }

    public void info(String... messages) {
        getModule(ConsoleLogger.class).info(messages);
    }

    public Plugin registerListeners(Listener... listeners) {
        if (listeners != null && listeners.length >= 1) {
            for (Listener listener : listeners) {
                listenerMap.put(listener.getClass(), listener);
            }
        }
        return this;
    }

    public void register() {
        for (Listener listener : listenerMap.values()) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
        getLogger().info("Registered " + listenerMap.size() + " listener(s).");
    }

    public void finish() {
        getLogger().info("Registered " + moduleMap.size() + " module(s).");
    }

    public Map<Class<?>, PluginModule> getModules() {
        return moduleMap;
    }

    public Map<Class<?>, Listener> getListeners() {
        return listenerMap;
    }

    public File getSchematicFolder() {
        return new File(getDataFolder(), "schematics");
    }

    public abstract void reload();

    public void saveConfiguration(FileConfiguration configuration, String child) {
        File file = new File(getDataFolder(), child);

        if (file.exists()) {
            PluginConsumer.process(
                () -> {
                    configuration.save(file);
                    reloadConfiguration();
                }
            );
        }
    }

    public FileConfiguration loadConfiguration(File folder, String child) {
        File file = new File(folder, child);

        FileUtilities.saveResource(
            file,
            child.startsWith("/") ?
                Plugin.class.getResourceAsStream(child) :
                Plugin.class.getResourceAsStream("/" + child)
        );

        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                if (file.createNewFile()) {
                    return YamlConfiguration.loadConfiguration(file);
                }
            } catch (IOException ignored) { }
            return new YamlConfiguration();
        }
    }

    public FileConfiguration loadConfiguration(File file) {
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                if (file.createNewFile()) {
                    return YamlConfiguration.loadConfiguration(file);
                }
            } catch (IOException ignored) { }
            return new YamlConfiguration();
        }
    }

    public FileConfiguration getStructures() {
        return structures;
    }
    public FileConfiguration getSettings() {
        return settings;
    }

    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public boolean isLegacy() { return (serverVersion.contains("v1_8") || serverVersion.contains("v1_9") || serverVersion.contains("v1_10") || serverVersion.contains("v1_11") || serverVersion.contains("v1_12")); }

    public File getWorldSettings() {
        return new File(getDataFolder(), "worlds");
    }

    public void log() {
        getLogger().info("Registered Custom Logger");
    }

    public void reloadConfigurations() {
        structures = loadConfiguration(getDataFolder(), "structures.yml");
        settings = loadConfiguration(getDataFolder(), "settings.yml");
    }

    public void reloadConfiguration() {
        settings = loadConfiguration(getDataFolder(), "settings.yml");
    }
}
