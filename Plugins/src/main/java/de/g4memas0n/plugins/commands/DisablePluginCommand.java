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

public class DisablePluginCommand implements CommandExecutor, TabCompleter {
    private static final int PLUGIN_ARG = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {
        if (sender instanceof BlockCommandSender) {
            PluginManager.logToConsole(Level.SEVERE, Messages.COMMAND_FROM_CONSOLE);
            return false;
        }

        if (sender.hasPermission(PluginManager$Command.DISABLE_PLUGIN.getPermission())) {
            if (args.length == PLUGIN_ARG) {
                String pluginName = args[PLUGIN_ARG - 1];
                Plugin plugin = PluginManager.getInstance().getPluginManager().getPlugin(pluginName);

                if (plugin != null) {
                    pluginName = plugin.getName();
                    if (plugin.isEnabled()) {
                        // Plugin is enabled and can be disabled.
                        PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_DISABLING, pluginName));

                        PluginManager.getInstance().getPluginManager().disablePlugin(plugin);

                        PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_DISABLE_SUCCESS,
                                pluginName));

                        sender.sendMessage(Messages.tl(false, "disable_success", pluginName));
                    } else {
                        // Plugin is already disabled.
                        sender.sendMessage(Messages.tl(true, "disable_error_alreadyDisabled", pluginName));
                    }
                } else {
                    // There is no Plugin with the specified plugin name.
                    sender.sendMessage(Messages.tl(true, "disable_error_noPlugin", pluginName));
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
        List<String> enabledPlugins = new ArrayList<>();

        if (args.length == PLUGIN_ARG) {
            // Searches for all enabled Plugins and put the name of them in to the enabledPlugins list.
            for (Plugin current : PluginManager.getInstance().getPluginManager().getPlugins()) {
                if (current.isEnabled()) {
                    if (current.getName().contains(args[PLUGIN_ARG - 1])) {
                        enabledPlugins.add(current.getName());
                    }
                }
            }
        }

        Collections.sort(enabledPlugins);
        return enabledPlugins;
    }
}