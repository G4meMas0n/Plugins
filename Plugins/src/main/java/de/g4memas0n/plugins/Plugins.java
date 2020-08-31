package de.g4memas0n.plugins;

import de.g4memas0n.plugins.command.BasicPluginCommand;
import de.g4memas0n.plugins.command.PluginCommand;
import de.g4memas0n.plugins.listener.BasicListener;
import de.g4memas0n.plugins.listener.FilterListener;
import de.g4memas0n.plugins.storage.configuration.Settings;
import de.g4memas0n.plugins.util.messages.Messages;
import de.g4memas0n.plugins.util.logging.BasicLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Plugins main class.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class Plugins extends JavaPlugin {

    private final BasicPluginCommand command;
    private final BasicListener listener;

    private final Pattern filter = Pattern.compile("\\.jar$");
    private final BasicLogger logger;
    private final File directory;

    private Settings settings;
    private Messages messages;

    private boolean loaded;
    private boolean enabled;

    public Plugins() {
        this.command = new PluginCommand();
        this.listener = new FilterListener();

        this.logger = new BasicLogger(super.getLogger(), "Plugin", "Plugins");
        this.directory = new File(this.getDataFolder(), "..");
    }

    public @NotNull Set<File> getArchives() {
        final Set<File> archives = new HashSet<>();

        for (final File archive : this.directory.listFiles(File::isFile)) {
            final Matcher match = this.filter.matcher(archive.getName());

            if (match.find()) {
                archives.add(archive);
            }
        }

        return archives;
    }

    public @Nullable File getArchive(@NotNull final String name) {
        final Matcher match = this.filter.matcher(name);

        if (match.find()) {
            final File archive = new File(this.directory, name);

            if (archive.exists()) {
                return archive;
            }
        }

        return null;
    }

    public @NotNull Settings getSettings() {
        return this.settings;
    }

    public @NotNull Messages getMessages() {
        return this.messages;
    }

    @Override
    public void onLoad() {
        if (this.loaded) {
            this.getLogger().severe("Tried to load plugin twice. Plugin is already loaded.");
            return;
        }

        this.settings = new Settings(this);
        this.settings.load();

        this.logger.setDebug(this.settings.isDebug());

        this.messages = new Messages(this.getDataFolder(), this.logger);
        this.messages.setLocale(this.settings.getLocale());

        this.loaded = true;
    }

    @Override
    public void onEnable() {
        if (this.enabled) {
            this.getLogger().severe("Tried to enable plugin twice. Plugin is already enabled.");
            return;
        }

        if (!this.loaded) {
            this.getLogger().warning("Plugin was not loaded. Loading it...");
            this.onLoad();
        }

        this.messages.enable();

        this.getLogger().debug("Register plugin command and listener...");

        this.command.register(this);
        this.listener.register(this);

        this.getLogger().debug("Plugin command and listener has been registered.");

        this.enabled = true;
    }

    @Override
    public void onDisable() {
        if (!this.enabled) {
            this.getLogger().severe("Tried to disable plugin twice. Plugin is already disabled.");
            return;
        }

        this.getLogger().debug("Unregister plugin command and listener...");

        this.command.unregister();
        this.listener.unregister();

        this.getLogger().debug("Plugin command and listener has been unregistered.");

        this.messages.disable();

        this.settings = null;
        this.messages = null;

        this.enabled = false;
        this.loaded = false;
    }

    @Override
    public @NotNull BasicLogger getLogger() {
        return this.logger;
    }

    @Override
    public void reloadConfig() {
        this.settings.load();

        this.logger.setDebug(this.settings.isDebug());
        this.messages.setLocale(this.settings.getLocale());
        this.command.getCommand().setPermissionMessage(this.messages.translate("noPermission"));
    }

    @Override
    public void saveConfig() {
        // Disabled, because it is not intended to save the config file, as this breaks the comments.
    }
}
