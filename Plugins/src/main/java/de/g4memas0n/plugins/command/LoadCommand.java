package de.g4memas0n.plugins.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.g4memas0n.plugins.util.Messages.tl;
import static de.g4memas0n.plugins.util.Messages.tlErr;

/**
 * The load command that allows to load plugins from their files.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class LoadCommand extends BasicCommand {

    private static final int ARCHIVE = 0;

    public LoadCommand() {
        super("load", 1, 1);

        this.setPermission("plugins.load");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender,
                           @NotNull final String[] arguments) {
        if (this.argsInRange(arguments.length)) {
            final File archive = this.getInstance().getArchive(arguments[ARCHIVE]);

            if (archive == null || !archive.exists()) {
                sender.sendMessage(tlErr("archiveNotFound", arguments[ARCHIVE]));
                return true;
            }

            try {
                final PluginDescriptionFile description = this.getInstance().getPluginLoader().getPluginDescription(archive);
                final Plugin plugin = this.getInstance().getServer().getPluginManager().getPlugin(description.getName());

                if (plugin != null && plugin.getDescription().getMain().equals(description.getMain())) {
                    sender.sendMessage(tl("loadAlready", plugin.getName(), archive.getName()));
                    return true;
                }

                if (description.getLoad() == PluginLoadOrder.STARTUP) {
                    sender.sendMessage(tl("loadStartup", description.getName(), archive.getName()));
                    return true;
                }

                for (final String name : description.getDepend()) {
                    final Plugin current = this.getInstance().getServer().getPluginManager().getPlugin(name);

                    if (current == null || current.isEnabled()) {
                        sender.sendMessage(tl("loadDependency", description.getName(), archive.getName(), name));
                        return true;
                    }
                }

                for (final String name : description.getSoftDepend()) {
                    final Plugin current = this.getInstance().getServer().getPluginManager().getPlugin(name);

                    if (current != null && current.isEnabled()) {
                        sender.sendMessage(tl("loadDependency", description.getName(), archive.getName(), name));
                        return true;
                    }
                }

                for (final String name : description.getLoadBefore()) {
                    final Plugin current = this.getInstance().getServer().getPluginManager().getPlugin(name);

                    if (current != null) {
                        sender.sendMessage(tl("loadBefore", description.getName(), archive.getName(), name));
                        return true;
                    }
                }

                final Plugin loaded = this.getInstance().getServer().getPluginManager().loadPlugin(archive);

                if (loaded != null) {
                    this.getInstance().getLogger().info(String.format("Got task to load plugin: Loading plugin '%s'...", loaded.getName()));
                    loaded.onLoad();
                    this.getInstance().getLogger().info(String.format("Please make sure that plugin '%s' was loaded correctly.", loaded.getName()));

                    sender.sendMessage(tl("loadPlugin", loaded.getName(), archive.getName()));
                    return true;
                }

                sender.sendMessage(tl("loadFailed", description.getName(), archive.getName()));
                return true;
            } catch (InvalidDescriptionException | InvalidPluginException ex) {
                sender.sendMessage(tlErr("invalidDescription", archive.getName()));
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender,
                                             @NotNull final String[] arguments) {
        if (arguments.length == ARCHIVE + 1) {
            final List<String> completion = new ArrayList<>();

            PluginDescriptionFile description;
            Plugin plugin;

            for (final File archive : this.getInstance().getArchives()) {
                if (!StringUtil.startsWithIgnoreCase(archive.getName(), arguments[ARCHIVE])) {
                    continue;
                }

                try {
                    description = this.getInstance().getPluginLoader().getPluginDescription(archive);
                } catch (InvalidDescriptionException ignored) {
                    continue;
                }

                plugin = this.getInstance().getServer().getPluginManager().getPlugin(description.getName());

                if (plugin == null || !plugin.getDescription().getMain().equals(description.getMain())) {
                    completion.add(archive.getName());
                }
            }

            Collections.sort(completion);

            return completion;
        }

        return Collections.emptyList();
    }
}
