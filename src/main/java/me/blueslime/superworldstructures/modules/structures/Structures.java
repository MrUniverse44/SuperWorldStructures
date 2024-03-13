package me.blueslime.superworldstructures.modules.structures;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.structures.type.Structure;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Structures extends PluginModule {
    private final Map<String, Structure> structureMap = new ConcurrentHashMap<>();

    public Structures(SuperWorldStructures plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        ConfigurationSection section = getSettings().getConfigurationSection("structures");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            structureMap.put(
                key,
                new Structure(
                    getSettings(),
                    "structures." + key,
                    key
                )
            );
        }
    }

    @Override
    public void shutdown() {
        structureMap.clear();
    }

    @Override
    public void reload() {
        shutdown();

        initialize();
    }

    public Structure fetchStructure(String id) {
        return structureMap.get(id);
    }
}
