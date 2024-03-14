package me.blueslime.superworldstructures.modules.worlds;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.settings.Settings;
import me.blueslime.superworldstructures.modules.settings.world.WorldSettings;
import me.blueslime.superworldstructures.modules.structures.Structures;
import me.blueslime.superworldstructures.modules.structures.type.Structure;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worlds extends PluginModule {
    private final Map<String, List<Structure>> worldStructures = new HashMap<>();

    public Worlds(SuperWorldStructures plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        shutdown();

        ConfigurationSection section = getSettings().getConfigurationSection("world-files");
        FileConfiguration settings = getSettings();

        if (isDebug()) {
            info("&9[Debug Mode] &7Loading worlds");
        }

        if (section == null) {
            return;
        }

        if (isDebug()) {
            info("&9[Debug Mode] &7Loading...");
        }

        for (String key : section.getKeys(false)) {
            String path = "world-files." + key + ".file-name";

            if (isDebug()) {
                info("&9[Debug Mode] &7Loading world: &f" + key);
            }

            if (!settings.contains(path)) {
                if (isDebug()) {
                    info("&9[Debug Mode] &7This world don't exists.");
                }
                continue;
            }

            WorldSettings worldSettings = getModule(Settings.class).fetchWorldSettings(settings.getString(path, ""));

            if (worldSettings == null) {
                if (isDebug()) {
                    info("&9[Debug Mode] &7Configuration settings: &f" + settings.getString(path, "") + "&7 don't exists.");
                }
                continue;
            }

            if (isDebug()) {
                info("&9[Debug Mode] &7Configuration Settings found! loading structures...");
            }

            List<Structure> structureList = new ArrayList<>();

            for (String id : worldSettings.getStructures()) {
                Structure structure = getModule(Structures.class).fetchStructure(id);

                if (isDebug()) {
                    info("&9[Debug Mode] &7Fetching world structure id: &f" + id + "&7 for world: &f" + key);
                }

                if (structure != null) {
                    if (isDebug()) {
                        info("&9[Debug Mode] &7Structure id: &f" + id + "&7 loaded for world: &f" + key);
                    }
                    structureList.add(structure);
                }
            }

            worldStructures.put(
                key,
                structureList
            );
        }
    }

    @Override
    public void shutdown() {
        worldStructures.clear();
    }

    public List<Structure> fetchWorld(String world) {
        return worldStructures.get(world);
    }

    @Override
    public void reload() {
        initialize();
    }
}
