package me.blueslime.superworldstructures.modules.settings;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.settings.world.WorldSettings;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Settings extends PluginModule {
    private final Map<String, WorldSettings> worldMap = new HashMap<>();

    public Settings(SuperWorldStructures plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        shutdown();

        if (isDebug()) {
            info("&9[Debug Mode] &7Loading world settings...");
        }

        if (!getPlugin().getWorldSettings().exists()) {
            if (isDebug()) {
                info("&9[Debug Mode] &7World Settings folder don't exists.");
            }
            return;
        }

        File[] files = getPlugin().getWorldSettings().listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            if (isDebug()) {
                info("&9[Debug Mode] &7Can't find world settings.");
            }
            return;
        }

        for (File file : files) {
            FileConfiguration configuration = getPlugin().loadConfiguration(file);

            WorldSettings settings = new WorldSettings(
                configuration.getBoolean("enabled", false),
                configuration.getStringList("enabled-structures")
            );

            if (isDebug()) {
                info("&9[Debug Mode] &7Loading world settings id: &f" + file.getName());
            }

            if (settings.isEnabled() && !settings.isEmpty()) {
                if (isDebug()) {
                    info("&9[Debug Mode] &7World Settings loaded: &f" + file.getName());
                }
                worldMap.put(
                        file.getName(),
                        settings
                );
            } else {
                if (isDebug()) {
                    info("&9[Debug Mode] &7Can't load settings for: &f" + file.getName() + "&7 because is disabled or isEmpty");
                }
            }
        }
    }

    public WorldSettings fetchWorldSettings(String fileName) {
        if (fileName.isEmpty()) {
            return null;
        }
        return worldMap.get(fileName);
    }

    @Override
    public void shutdown() {
        worldMap.clear();
    }

    @Override
    public void reload() {
        initialize();
    }
}
