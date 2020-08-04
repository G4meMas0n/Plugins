package de.g4memas0n.plugins;

import de.g4memas0n.plugins.manager.ConfigManager;
import de.g4memas0n.plugins.manager.FileManager;
import de.g4memas0n.plugins.manager.Messages;
import de.g4memas0n.plugins.manager.SettingManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.logging.Level;

public class PluginManager extends JavaPlugin {
    private static PluginManager instance;
    private org.bukkit.plugin.PluginManager pluginManager;
    private SettingManager settingManager;
    private Messages messages;

    public static PluginManager getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.pluginManager = this.getServer().getPluginManager();
        ConfigManager configManager = new ConfigManager();

        try {
            configManager.load(FileManager.getConfigFile());
            this.setConfigManager(configManager);
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, String.format(Messages.CONFIG_LOAD_FAILURE,
                    Messages.PLUGIN_SELF_DISABLED));
            logStackTrace(ex);
            pluginManager.disablePlugin(this);
        }

        try {
            PluginManager$Command.initCommands();
        } catch (NullPointerException ex) {
            this.getLogger().log(Level.WARNING, String.format(Messages.COMMAND_INIT_FAILURE,
                    Messages.PLUGIN_SELF_DISABLED));
            logStackTrace(ex);
            pluginManager.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @NotNull
    public org.bukkit.plugin.PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public void setConfigManager(@NotNull ConfigManager configManager) {
        this.settingManager = configManager.getSettingManager();
        this.messages = configManager.getMessages();
        this.messages.setInstance(this.messages);
    }

    @Nullable
    public Messages getMessages() {
        return this.messages;
    }

    public static void logStackTrace(@NotNull Exception exception) {
        if (getInstance().settingManager == null || getInstance().settingManager.isPrintStackTrace()) {
            getInstance().getLogger().info(Messages.CHECK_STACK_TRACE);
            getInstance().getLogger().info(exception.toString());
            for (StackTraceElement currentElement : exception.getStackTrace()) {
                getInstance().getLogger().info("    at " + currentElement.toString());
            }
        }
    }

    public static void logToConsole(Level level, String message) {
        if (getInstance().settingManager == null || getInstance().settingManager.isLogToConsole()) {
            getInstance().getLogger().log(level, message);
        }
    }
}