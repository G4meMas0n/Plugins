package de.g4memas0n.plugins.commands;

import de.g4memas0n.plugins.PluginManager;
import de.g4memas0n.plugins.PluginManager$Command;
import de.g4memas0n.plugins.manager.ConfigManager;
import de.g4memas0n.plugins.manager.FileManager;
import de.g4memas0n.plugins.manager.Messages;
import de.g4memas0n.plugins.PluginManager$Permission;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MainCommand implements CommandExecutor, TabCompleter {
    private static final int SUB_COMMAND_ARG = 1;
    private static final String SUB_COMMAND_VERSION = "version";
    private static final String SUB_COMMAND_RELOAD = "reload";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {
        if (sender instanceof BlockCommandSender) {
            PluginManager.logToConsole(Level.SEVERE, Messages.COMMAND_FROM_CONSOLE);
            return false;
        }

        if (sender.hasPermission(PluginManager$Command.MAIN_COMMAND.getPermission())) {
            if (args.length == SUB_COMMAND_ARG) {
                switch (args[SUB_COMMAND_ARG - 1]) {
                    case SUB_COMMAND_VERSION:
                        this.executeVersion(sender);
                        return true;

                    case SUB_COMMAND_RELOAD:
                        this.executeReload(sender);
                        return true;
                }
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
        List<String> availableSubCommands = new ArrayList<>();

        if (args.length == SUB_COMMAND_ARG) {
            if (sender.hasPermission(PluginManager$Command.MAIN_COMMAND.getPermission()
                    .concat(PluginManager$Permission.MAIN_VERSION.getSuffix()))) {
                if (SUB_COMMAND_VERSION.contains(args[SUB_COMMAND_ARG - 1])) {
                    availableSubCommands.add(SUB_COMMAND_VERSION);
                }
            }

            if (sender.hasPermission(PluginManager$Command.MAIN_COMMAND.getPermission()
                    .concat(PluginManager$Permission.MAIN_RELOAD.getSuffix()))) {
                if (SUB_COMMAND_RELOAD.contains(args[SUB_COMMAND_ARG - 1])) {
                    availableSubCommands.add(SUB_COMMAND_RELOAD);
                }
            }
        }

        return availableSubCommands;
    }

    private void executeVersion(@NotNull CommandSender sender) {
        if (sender.hasPermission(PluginManager$Command.MAIN_COMMAND.getPermission()
                .concat(PluginManager$Permission.MAIN_VERSION.getSuffix()))) {
            sender.sendMessage(Messages.tl(false, "plugin_version", "Server",
                    PluginManager.getInstance().getServer().getVersion()));
            sender.sendMessage(Messages.tl(false, "plugin_version",
                    PluginManager.getInstance().getDescription().getName(),
                    PluginManager.getInstance().getDescription().getVersion()));
        } else {
            // The command sender is not permitted to use this sub command.
            sender.sendMessage(Messages.tl(false, "global_noPermission"));
        }
    }

    private void executeReload(@NotNull CommandSender sender) {
        if (sender.hasPermission(PluginManager$Command.MAIN_COMMAND.getPermission()
                .concat(PluginManager$Permission.MAIN_RELOAD.getSuffix()))) {
            ConfigManager configManager = new ConfigManager();

            try {
                configManager.load(FileManager.getConfigFile());
                PluginManager.getInstance().setConfigManager(configManager);
                sender.sendMessage(Messages.tl(false, "plugin_reload_success"));
            } catch (IOException ex) {
                PluginManager.getInstance().getLogger().log(Level.INFO, String.format(Messages.CONFIG_LOAD_FAILURE,
                        Messages.CONFIG_USE_PREVIOUS));
                PluginManager.logStackTrace(ex);

                sender.sendMessage(Messages.tl(true, "plugin_reload_error"));
            }
        } else {
            // The command sender is not permitted to use this sub command.
            sender.sendMessage(Messages.tl(false, "global_noPermission"));
        }
    }
}