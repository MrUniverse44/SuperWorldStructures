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
        if (!getPlugin().getWorldSettings().exists()) {
            return;
        }

        File[] files = getPlugin().getWorldSettings().listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            FileConfiguration configuration = getPlugin().loadConfiguration(file);

            WorldSettings settings = new WorldSettings(
                    configuration.getBoolean("enabled", false),
                    configuration.getStringList("enabled-structures")
            );

            if (settings.isEnabled() && !settings.isEmpty()) {
                worldMap.put(
                    file.getName(),
                    settings
                );
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
        shutdown();
        initialize();
    }
}
