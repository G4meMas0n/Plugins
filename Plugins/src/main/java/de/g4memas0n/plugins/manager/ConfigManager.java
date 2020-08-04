package de.g4memas0n.plugins.manager;

import de.g4memas0n.plugins.PluginManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

public class ConfigManager {
    private Messages messages;
    private SettingManager settingManager;

    public ConfigManager() {
        this.settingManager = new SettingManager();
    }

    public void load(@NotNull File configFile) throws IOException {
        PluginManager.getInstance().getLogger().log(Level.INFO, Messages.CONFIG_LOADING);
        YamlConfiguration yamlConfig = new YamlConfiguration();

        try {
            if (configFile.exists()) {
                yamlConfig.load(configFile);
            } else {
                PluginManager.getInstance().saveDefaultConfig();
            }
        } catch (InvalidConfigurationException ex) {
            throw new IOException(ex);
        }

        this.messages = new Messages(this.getLocale(this.getString(yamlConfig, "locale")));

        this.settingManager.setLogToConsole(this.getBoolean(yamlConfig, "debug.logToConsole"));
        this.settingManager.setPrintStackTrace(this.getBoolean(yamlConfig, "debug.printStackTrace"));

        PluginManager.getInstance().getLogger().log(Level.INFO, Messages.CONFIG_LOAD_SUCCESS);
    }

    private String getString(@NotNull YamlConfiguration yamlConfig, @NotNull String path) throws IOException {
        if (yamlConfig.contains(path)) {
            return yamlConfig.getString(path);
        } else {
            PluginManager.getInstance().getLogger().log(Level.INFO,
                    String.format(Messages.CONFIG_MISSING_PATH, path));

            if (this.getDefaults().contains(path)) {
                return this.getDefaults().getString(path);
            } else {
                throw new IOException("Found wrong path: " + path);
            }
        }
    }

    private boolean getBoolean(@NotNull YamlConfiguration yamlConfig, @NotNull String path) throws IOException {
        if (yamlConfig.contains(path)) {
            return yamlConfig.getBoolean(path);
        } else {
            PluginManager.getInstance().getLogger().log(Level.INFO,
                    String.format(Messages.CONFIG_MISSING_PATH, path));

            if (this.getDefaults().contains(path)) {
                return this.getDefaults().getBoolean(path);
            } else {
                throw new IOException("Found wrong path: " + path);
            }
        }
    }

    @Nullable
    private Locale getLocale(@NotNull String locale) {
        if (!locale.isEmpty()) {
            String[] parts = locale.split("_");

            if (parts.length == 1) {
                return new Locale(parts[0]);
            } else if (parts.length == 2) {
                return new Locale(parts[0], parts[1]);
            } else if (parts.length == 3) {
                return new Locale(parts[0], parts[1], parts[2]);
            }
        }

        return null;
    }

    @NotNull
    private MemoryConfiguration getDefaults() {
        MemoryConfiguration defaults = new MemoryConfiguration();
        defaults.set("locale", "en");
        defaults.set("debug.logToConsole", true);
        defaults.set("debug.printStackTrace", false);

        return defaults;
    }

    @NotNull
    public Messages getMessages() {
        return messages;
    }

    @NotNull
    public SettingManager getSettingManager() {
        return settingManager;
    }
}