package de.g4memas0n.plugins.configuration;

import de.g4memas0n.plugins.Plugins;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Settings class that represent the configuration file of this plugin.
 *
 * @author G4meMason
 * @since Release 2.0.0
 */
public final class Settings {

    private static final String CONFIG = "config.yml";

    private final Plugins instance;
    private final YamlConfiguration storage;

    private Set<String> commands;

    private String message;

    private boolean namespaces;
    private boolean debug;

    public Settings(@NotNull final Plugins instance) {
        this.instance = instance;
        this.storage = new YamlConfiguration();
    }

    public void load() {
        final File config = new File(this.instance.getDataFolder(), CONFIG);

        try {
            this.storage.load(config);

            this.instance.getLogger().info("Loaded configuration file: " + config.getName());
        } catch (FileNotFoundException ex) {
            this.instance.getLogger().warning("Unable to find configuration file: " + config.getName() + " (Saving default configuration...)");
            this.instance.saveResource(config.getName(), true);
            this.instance.getLogger().info("Saved default configuration from template: " + config.getName());

            this.load();
            return;
        } catch (InvalidConfigurationException ex) {
            this.instance.getLogger().warning("Unable to load broken configuration file: " + config.getName() + " (Renaming it and saving default configuration...)");

            final File broken = new File(config.getParent(), config.getName().replaceAll("(?i)(yml)$", "broken.$1"));

            if (broken.exists() && broken.delete()) {
                this.instance.getLogger().info("Deleted old broken configuration file: " + broken.getName());
            }

            if (config.renameTo(broken)) {
                this.instance.getLogger().info("Renamed broken configuration file to: " + broken.getName());
            }

            this.instance.saveResource(config.getName(), true);
            this.instance.getLogger().info("Saved default configuration from template: " + config.getName());

            this.load();
            return;
        } catch (IOException ex) {
            this.instance.getLogger().warning("Unable to load configuration file: " + config.getName() + " (Loading default configuration...)");

            /*
             * Removing each key manual to clear existing configuration, as loading a blank config does not work here
             * for any reason.
             */
            this.storage.getKeys(false).forEach(key -> this.storage.set(key, null));

            this.instance.getLogger().info("Loaded default configuration from template: " + config.getName());
        }

        this.commands = this._getFilterCommands();
        this.message = this._getFilterMessage();
        this.namespaces = this._getFilterNamespaces();

        this.debug = this._getDebug();
    }

    @SuppressWarnings("unused")
    public void save() {
        /*
         * Disabled, because it is not intended to save the config file, as this breaks the comments.
         */

        //final File config = new File(this.instance.getDataFolder(), CONFIG);
        //
        //try {
        //    this.storage.save(config);
        //} catch (IOException ex) {
        //    this.instance.getLogger().warning("Unable to save configuration file: " + config.getName() + " (" + ex.getMessage() + ")");
        //}
    }

    protected boolean _getDebug() {
        return this.storage.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return this.debug;
    }

    protected @NotNull Locale _getLocale() {
        final String locale = this.storage.getString("locale");

        if (locale != null && !locale.isEmpty()) {
            final Matcher match = Pattern.compile("^([a-zA-Z]{2,8})([_-]([a-zA-Z]{2}|[0-9]{3}))?$").matcher(locale);

            if (match.matches()) {
                return match.group(3) == null ? new Locale(match.group(1)) : new Locale(match.group(1), match.group(3));
            }

            this.instance.getLogger().warning("Detected invalid locale: Locale does not match regex.");
        }

        return Locale.ENGLISH;
    }

    public @NotNull Locale getLocale() {
        return this._getLocale();
    }

    // Filter-Commands-Setting Methods.
    protected @NotNull Set<String> _getFilterCommands() {
        return Collections.unmodifiableSet(new HashSet<>(this.storage.getStringList("filter.commands")));
    }

    public boolean isFilterCommands() {
        return !this.commands.isEmpty();
    }

    public boolean isFilterCommand(@NotNull final String command) {
        return this.commands.contains(command.toLowerCase());
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
}
