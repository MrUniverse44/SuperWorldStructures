package me.blueslime.superworldstructures.modules.commands.list;

import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import me.blueslime.utilitiesapi.tools.PluginTools;
import org.bukkit.entity.Player;

import java.util.List;
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
                "&6/" + command + " find &7- &eFind structures in this world",
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

        if (argument.equals("find") && sender.isPlayer()) {
            Player player = sender.toPlayer();

            String worldName = player.getWorld().getName();

            if (!getPlugin().getSettings().contains("spawned-structures." + worldName)) {
                sender.send("&cNo structures found in this world.");
                return;
            }

            sender.send("&6Showing structures for world: &f" + worldName);

            List<String> structures = getPlugin().getSettings().getStringList("spawned-structures." + worldName);

            for (String location : structures) {
                String[] split = location.replace(" ", "").split(";");

                String id = split[0].replace("Structure:", "")
                        .replace(" ", "");

                String defValues = split.length >= 2 ? split[1] : "x=0y=0z=0";

                defValues = defValues.replace("x=", "")
                        .replace("y=", ",")
                        .replace("z=", ",")
                        .replace(" ", "");

                String[] splitValues = defValues.split(",");

                int x = splitValues.length >= 1 && !splitValues[0].isEmpty() ? PluginTools.isNumber(splitValues[0]) ? Integer.parseInt(splitValues[0]) : 0 : 0;
                int y = splitValues.length >= 2 && !splitValues[1].isEmpty() ? PluginTools.isNumber(splitValues[1]) ? Integer.parseInt(splitValues[1]) : 0 : 0;
                int z = splitValues.length >= 3 && !splitValues[1].isEmpty() ? PluginTools.isNumber(splitValues[2]) ? Integer.parseInt(splitValues[2]) : 0 : 0;

                sender.send(
                    "  &eStructure ID: &7" + id + " &ex: &b" + x + "&e, y:&b " + y + "&e, z:&b " + z
                );
            }
            sender.send("&6The plugin found &a" + structures.size() + "&6 structures in this world.");
            return;
        }

        sender.send(
            "&eUnknown argument! &6Please use /sws help!&f for the command list."
        );
    }
}
