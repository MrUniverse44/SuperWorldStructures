package me.blueslime.superworldstructures.modules.commands.list;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import me.blueslime.utilitiesapi.commands.sender.Sender;

import java.util.Locale;

public class MainCommand extends AdvancedCommand<SuperWorldStructures> {
    private final String command;
    public MainCommand(SuperWorldStructures plugin, String command) {
        super(plugin, command);
        this.command = command;
    }

    @Override
    public void executeCommand(Sender sender, String label, String[] arguments) {
        if (
            !sender.hasPermission("superworldstructures.admin") && !sender.hasPermission("superworldstructures.*") &&
            !sender.hasPermission("superworldstructures.admin.*") && !sender.hasPermission("superworldstructures.command")
        ) {
            sender.send("&cYou don't have permissions for this action.");
            return;
        }
        if (arguments.length == 0 || arguments[0].equalsIgnoreCase("help")) {
            sender.send(
            "&6/" + command + " reload &7- &eReload command",
                "&6",
                "&f&oCreated by JustJustin. Version: " + getPlugin().getDescription().getVersion()
            );
            return;
        }
        String argument = arguments[0].toLowerCase(Locale.ENGLISH);

        if (argument.equals("reload")) {

            getPlugin().reload();

            sender.send(
                "&aPlugin has been reloaded successfully."
            );

            return;
        }

        sender.send(
            "&eUnknown argument! &6Please use /sws help!&f for the command list."
        );
    }
}
