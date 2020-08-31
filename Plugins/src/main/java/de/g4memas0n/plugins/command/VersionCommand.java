package de.g4memas0n.plugins.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.g4memas0n.plugins.util.messages.Messages.tl;
import static de.g4memas0n.plugins.util.messages.Messages.tlErr;

/**
 * The version command that allows to show the version of plugins.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class VersionCommand extends BasicCommand {

    private static final int PLUGIN = 0;

    public VersionCommand() {
        super("version", 0, 1);

        this.setPermission("plugins.version");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender,
                           @NotNull final String[] arguments) {
        if (this.argsInRange(arguments.length)) {
            final Plugin plugin = arguments.length == this.getMinArgs() ? this.getInstance()
                    : this.getInstance().getServer().getPluginManager().getPlugin(arguments[PLUGIN]);

            if (plugin == null) {
                sender.sendMessage(tlErr("pluginNotFound", arguments[PLUGIN]));
                return true;
            }

            sender.sendMessage(tl("versionInfo", plugin.getName(), plugin.getDescription().getVersion()));

            if (plugin.equals(this.getInstance())) {
                sender.sendMessage(tl("versionServer", this.getInstance().getServer().getName(),
                        this.getInstance().getServer().getBukkitVersion(),
                        this.getInstance().getServer().getVersion()));
            }

            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender,
                                             @NotNull final String[] arguments) {
        if (arguments.length == PLUGIN + 1) {
            final List<String> completion = new ArrayList<>();

            for (final Plugin plugin : this.getInstance().getServer().getPluginManager().getPlugins()) {
                if (StringUtil.startsWithIgnoreCase(plugin.getName(), arguments[PLUGIN])) {
                    completion.add(plugin.getName());
                }
            }

            return completion;
        }

        return Collections.emptyList();
    }
}
