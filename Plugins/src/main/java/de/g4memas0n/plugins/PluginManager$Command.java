package de.g4memas0n.plugins;

import de.g4memas0n.plugins.commands.DisablePluginCommand;
import de.g4memas0n.plugins.commands.EnablePluginCommand;
import de.g4memas0n.plugins.commands.LoadPluginCommand;
import de.g4memas0n.plugins.commands.MainCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public enum PluginManager$Command {
    DISABLE_PLUGIN("disablePlugin", PluginManager$Permission.DISABLE_PLUGIN, new DisablePluginCommand()),
    ENABLE_PLUGIN("enablePlugin", PluginManager$Permission.ENABLE_PLUGIN, new EnablePluginCommand()),
    LOAD_PLUGIN("loadPlugin", PluginManager$Permission.LOAD_PLUGIN, new LoadPluginCommand()),
    MAIN_COMMAND("pluginManager", PluginManager$Permission.MAIN, new MainCommand());

    private PluginCommand command;
    private String permission;
    private CommandExecutor commandExecutor;
    private TabCompleter tabCompleter;

    PluginManager$Command(@NotNull final String name, @NotNull PluginManager$Permission permission,
                          @NotNull Object commandClass) {
        this.command = PluginManager.getInstance().getCommand(name);
        this.permission = permission.toString();
        this.commandExecutor = (CommandExecutor) commandClass;
        this.tabCompleter = (TabCompleter) commandClass;
    }

    public String getPermission() {
        return command.getPermission() != null ? command.getPermission() : permission;
    }

    protected static void initCommands() {
        for (PluginManager$Command current : PluginManager$Command.values()) {
            current.command.setExecutor(current.commandExecutor);
            current.command.setTabCompleter(current.tabCompleter);
        }
    }

    public static void setPermissionMessage(@NotNull String message) {
        for (PluginManager$Command current : PluginManager$Command.values()) {
            current.command.setPermissionMessage(message);
        }
    }

    public static void setUsageMessage(@NotNull String message) {
        for (PluginManager$Command current : PluginManager$Command.values()) {
            current.command.setUsage(MessageFormat.format(message, current.command.getName(),
                    current.command.getDescription(),
                    current.command.getUsage().replace("<command>", current.command.getName())));
        }
    }
}