package me.blueslime.superworldstructures.modules.commands;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.PluginModule;
import me.blueslime.superworldstructures.modules.commands.list.MainCommand;
import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;

import java.util.ArrayList;
import java.util.List;

public class Commands extends PluginModule {
    private final List<AdvancedCommand<SuperWorldStructures>> commandList = new ArrayList<>();
    public Commands(SuperWorldStructures plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        commandList.add(new MainCommand(getPlugin(), "sws"));
        commandList.add(new MainCommand(getPlugin(), "SuperWorldStructures"));

        PluginConsumer.process(
            () -> {
                for (AdvancedCommand<SuperWorldStructures> command : commandList) {
                    command.register();
                }
            }
        );
    }

    @Override
    public void shutdown() {
        PluginConsumer.process(
            () -> {
                for (AdvancedCommand<SuperWorldStructures> command : commandList) {
                    command.unregister();
                }
            }
        );

        commandList.clear();
    }

    @Override
    public void reload() {
        shutdown();
        initialize();
    }
}
