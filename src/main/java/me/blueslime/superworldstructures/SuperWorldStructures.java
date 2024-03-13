package me.blueslime.superworldstructures;

import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.commands.Commands;
import me.blueslime.superworldstructures.modules.listeners.world.ChunkPopulateListener;
import me.blueslime.superworldstructures.modules.logger.ConsoleLogger;
import me.blueslime.superworldstructures.modules.plugin.Plugin;
import me.blueslime.superworldstructures.modules.settings.Settings;
import me.blueslime.superworldstructures.modules.structures.Structures;
import me.blueslime.superworldstructures.modules.worlds.Worlds;

public final class SuperWorldStructures extends Plugin {

    @Override
    public void onEnable() {
       initialize(this);
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    @Override
    public void registerModules() {

        // Register first the module instance.

        registerModule(
            new ConsoleLogger(this)
        ).log();

        // Register other modules.

        registerModule(
            new Structures(this),
            new Settings(this),
            new Worlds(this),
            new Commands(this)
        ).finish();

        // Register listeners.

        registerListeners(
            new ChunkPopulateListener(this)
        ).register();
    }

    @Override
    public void reload() {
        reloadConfigurations();

        for (PluginModule module : getModules().values()) {
            module.reload();
        }
    }
}
