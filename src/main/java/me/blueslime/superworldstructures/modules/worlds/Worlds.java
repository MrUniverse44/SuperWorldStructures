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
        ConfigurationSection section = getSettings().getConfigurationSection("world-files");
        FileConfiguration settings = getSettings();


        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            String path = "world-files." + key + ".file-name";

            if (!settings.contains(path)) {
                continue;
            }

            WorldSettings worldSettings = getModule(Settings.class).fetchWorldSettings(settings.getString(path, ""));

            if (worldSettings == null) {
                continue;
            }

            List<Structure> structureList = new ArrayList<>();

            for (String id : worldSettings.getStructures()) {
                Structure structure = getModule(Structures.class).fetchStructure(id);

                if (structure != null) {
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
        shutdown();
        initialize();
    }
}
