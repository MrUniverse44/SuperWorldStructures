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
        shutdown();

        ConfigurationSection section = getSettings().getConfigurationSection("structures");

        if (isDebug()) {
            info("&9[Debug Mode] &7Loading Structures module");
        }

        if (section == null) {
            if (isDebug()) {
                info("&9[Debug Mode] &7Can't find structures section");
            }
            return;
        }

        if (isDebug()) {
            info("&9[Debug Mode] &7Loading structures...");
        }

        for (String key : section.getKeys(false)) {
            info("&9[Debug Mode] &7Structure id: '&f" + key + "&7' has been loaded.");
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
        initialize();
    }

    public Structure fetchStructure(String id) {
        return structureMap.get(id);
    }
}
