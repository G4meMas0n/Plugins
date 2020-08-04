package de.g4memas0n.plugins.commands;

import de.g4memas0n.plugins.PluginManager;
import de.g4memas0n.plugins.PluginManager$Command;
import de.g4memas0n.plugins.manager.Messages;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class EnablePluginCommand implements CommandExecutor, TabCompleter {
    private static final int PLUGIN_ARG = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (sender instanceof BlockCommandSender) {
            PluginManager.logToConsole(Level.SEVERE, Messages.COMMAND_FROM_CONSOLE);
            return false;
        }

        if (sender.hasPermission(PluginManager$Command.ENABLE_PLUGIN.getPermission())) {
            if (args.length == PLUGIN_ARG) {
                String pluginName = args[PLUGIN_ARG - 1];
                Plugin plugin = PluginManager.getInstance().getPluginManager().getPlugin(pluginName);

                if (plugin != null) {
                    pluginName = plugin.getName();
                    if (!plugin.isEnabled()) {
                        // Plugin is disabled and can be enabled.
                        PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_ENABLING, pluginName));

                        PluginManager.getInstance().getPluginManager().enablePlugin(plugin);

                        PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_ENABLE_SUCCESS,
                                pluginName));

                        sender.sendMessage(Messages.tl(false, "enable_success", pluginName));
                    } else {
                        // Plugin is already enabled.
                        sender.sendMessage(Messages.tl(true, "enable_error_alreadyEnabled", pluginName));
                    }
                } else {
                    // There is no Plugin with the specified plugin name.
                    // Either it does not exist or it is not loaded
                    sender.sendMessage(Messages.tl(true, "enable_error_noPlugin", pluginName));
                }

                return true;
            }

            return false;
        } else {
            // The command sender is not permitted to use this command.
            sender.sendMessage(Messages.tl(false, "global_noPermission"));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      @NotNull String[] args) {
        List<String> disabledPlugins = new ArrayList<>();

        if (args.length == PLUGIN_ARG) {
            // Searches for all disabled Plugins and put the name of them in to the disabledPlugins list.
            for (Plugin current : PluginManager.getInstance().getPluginManager().getPlugins()) {
                if (!current.isEnabled()) {
                    if (current.getName().contains(args[PLUGIN_ARG - 1])) {
                        disabledPlugins.add(current.getName());
                    }
                }
            }
        }

        Collections.sort(disabledPlugins);
        return disabledPlugins;
    }
}