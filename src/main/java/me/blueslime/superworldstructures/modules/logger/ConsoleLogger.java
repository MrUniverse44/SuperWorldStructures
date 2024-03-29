package me.blueslime.superworldstructures.modules.logger;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.utilitiesapi.text.TextUtilities;

public class ConsoleLogger extends PluginModule {
    public ConsoleLogger(SuperWorldStructures plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void reload() {

    }

    @Override
    public ConsoleLogger getLogger() {
        return this;
    }

    @Override
    public void info(String... messages) {
        for (String message : messages) {
            getServer().getConsoleSender().sendMessage(
                    TextUtilities.colorize("&3[&bSuperWorldStructures&3]&f " + message)
            );
        }
    }
}
