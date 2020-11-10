package de.g4memas0n.plugins;

import de.g4memas0n.plugins.command.BasicPluginCommand;
import de.g4memas0n.plugins.command.PluginCommand;
import de.g4memas0n.plugins.listener.BasicListener;
import de.g4memas0n.plugins.listener.FilterListener;
import de.g4memas0n.plugins.configuration.Settings;
import de.g4memas0n.plugins.util.messages.Messages;
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

    private final Pattern filter = Pattern.compile("\\.jar$");

    private final BasicPluginCommand command;
    private final BasicListener listener;
    private final File directory;

    private Settings settings;
    private Messages messages;

    private boolean loaded;
    private boolean enabled;

    public Plugins() {
        this.command = new PluginCommand();
        this.listener = new FilterListener();
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

        this.messages = new Messages(this.getDataFolder(), this.getLogger());
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

        if (this.settings.isDebug()) {
            this.getLogger().info("Register plugin command and listeners...");
        }

        this.command.register(this);
        this.listener.register(this);

        if (this.settings.isDebug()) {
            this.getLogger().info("Plugin command and listeners has been registered.");
        }

        this.enabled = true;
    }

    @Override
    public void onDisable() {
        if (!this.enabled) {
            this.getLogger().severe("Tried to disable plugin twice. Plugin is already disabled.");
            return;
        }

        if (this.settings.isDebug()) {
            this.getLogger().info("Unregister plugin command and listeners...");
        }

        this.command.unregister();
        this.listener.unregister();

        if (this.settings.isDebug()) {
            this.getLogger().info("Plugin command and listeners has been unregistered.");
        }

        this.messages.disable();

        this.settings = null;
        this.messages = null;

        this.enabled = false;
        this.loaded = false;
    }

    @Override
    public void reloadConfig() {
        this.settings.load();
        this.messages.setLocale(this.settings.getLocale());
        this.command.getCommand().setPermissionMessage(this.messages.translate("noPermission"));
    }

    @Override
    public void saveConfig() {
        // Disabled, because it is not intended to save the config file, as this breaks the comments.
    }
}
