package de.g4memas0n.plugins.storage.configuration;

import de.g4memas0n.plugins.Plugins;
import de.g4memas0n.plugins.storage.YamlStorageFile;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The Settings class that represent the configuration file of this plugin.
 *
 * @author G4meMason
 * @since Release 2.0.0
 */
public final class Settings {

    private static final String FILE_CONFIG = "config.yml";
    private static final String FILE_CONFIG_BROKEN = "config.broken.yml";

    private final Plugins instance;
    private final YamlStorageFile storage;

    private List<String> commands;
    private String message;
    private boolean namespaces;

    public Settings(@NotNull final Plugins instance) {
        this.instance = instance;
        this.storage = new YamlStorageFile(new File(instance.getDataFolder(), FILE_CONFIG));
    }

    @SuppressWarnings("unused")
    public void delete() {
        try {
            this.storage.delete();

            this.instance.getLogger().debug("Deleted configuration file: " + this.storage.getFile().getName());
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to delete configuration file '%s': %s",
                    this.storage.getFile().getName(), ex.getMessage()));
        }
    }

    public void load() {
        try {
            this.storage.load();

            this.instance.getLogger().debug("Loaded configuration file: " + this.storage.getFile().getName());
        } catch (FileNotFoundException ex) {
            this.instance.getLogger().warning(String.format("Unable to find configuration file '%s'. "
                    + "Saving default configuration...", this.storage.getFile().getName()));

            this.instance.saveResource(FILE_CONFIG, true);
            this.instance.getLogger().info(String.format("Saved default configuration from template: %s", FILE_CONFIG));
            this.load();
        } catch (InvalidConfigurationException ex) {
            this.instance.getLogger().severe(String.format("Unable to load configuration file '%s', because it is broken. "
                    + "Renaming it and saving default configuration...", this.storage.getFile().getName()));

            final File broken = new File(this.instance.getDataFolder(), FILE_CONFIG_BROKEN);

            if (broken.exists() && broken.delete()) {
                this.instance.getLogger().debug("Deleted old broken configuration file: " + broken.getName());
            }

            if (this.storage.getFile().renameTo(broken)) {
                this.instance.getLogger().info(String.format("Renamed broken configuration file '%s' to: %s",
                        this.storage.getFile().getName(), broken.getName()));
            }

            this.instance.saveResource(FILE_CONFIG, true);
            this.instance.getLogger().info(String.format("Saved default configuration from template: %s", FILE_CONFIG));
            this.load();
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to load configuration file '%s'. "
                    + "Loading default configuration...", this.storage.getFile().getName()));

            this.storage.clear();
        }

        this.commands = this._getFilterCommands();
        this.message = this._getFilterMessage();
        this.namespaces = this._getFilterNamespaces();
    }

    @SuppressWarnings("unused")
    public void save() {
        /*
        Disabled, because it is not intended to save the config file, as this breaks the comments.
        try {
            this.storage.save();
            this.instance.getLogger().debug("Saved configuration file: " + this.storage.getFile().getName());
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to save configuration file '%s': %s",
                    this.storage.getFile().getName(), ex.getMessage()));
        }
         */
    }

    private boolean _getDebug() {
        return this.storage.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return this._getDebug();
    }

    protected @NotNull Locale _getLocale() {
        final Locale locale = this.storage.getLocale("locale");

        if (locale == null) {
            return Locale.ENGLISH;
        }

        return locale;
    }

    public @NotNull Locale getLocale() {
        return this._getLocale();
    }

    protected @NotNull String _getFilterMessage() {
        final String message = this.storage.getString("filter.message");

        if (message == null) {
            return "§cYou are not permitted to access that command.";
        }

        return message;
    }

    public @NotNull String getFilterMessage() {
        return this.message;
    }

    protected boolean _getFilterNamespaces() {
        return this.storage.getBoolean("filter.namespaces", false);
    }

    public boolean isFilterNamespaces() {
        return this.namespaces;
    }

    protected @NotNull List<String> _getFilterCommands() {
        return Collections.unmodifiableList(this.storage.getStringList("filter.commands"));
    }

    public @NotNull List<String> getFilterCommands() {
        return this.commands;
    }

    public boolean isFilterCommand(@NotNull final String command) {
        return this.commands.contains(command.startsWith("/") ? command.substring(1) : command);
    }
}
