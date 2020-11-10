package de.g4memas0n.plugins.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.g4memas0n.plugins.util.Messages.tl;
import static de.g4memas0n.plugins.util.Messages.tlErr;
import static de.g4memas0n.plugins.util.Messages.tlJoin;

/**
 * The info command that allows to show the information's of plugins.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class InfoCommand extends BasicCommand {

    private static final int PLUGIN = 0;

    public InfoCommand() {
        super("info", 1, 1);

        this.setPermission("plugins.info");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender,
                           @NotNull final String[] arguments) {
        if (this.argsInRange(arguments.length)) {
            final Plugin plugin = this.getInstance().getServer().getPluginManager().getPlugin(arguments[PLUGIN]);

            if (plugin == null) {
                sender.sendMessage(tlErr("pluginNotFound", arguments[PLUGIN]));
                return true;
            }

            sender.sendMessage(tl("infoHeader", plugin.getName()));

            if (plugin.getDescription().getDescription() != null) {
                sender.sendMessage(tl("infoDescription", plugin.getDescription().getDescription()));
            }

            sender.sendMessage(tl("infoVersion", plugin.getDescription().getVersion()));
            sender.sendMessage(tlJoin("infoAuthors", plugin.getDescription().getAuthors()));

            if (plugin.getDescription().getWebsite() != null) {
                sender.sendMessage(tl("infoWebsite", plugin.getDescription().getWebsite()));
            }

            if (!plugin.getDescription().getDepend().isEmpty()) {
                sender.sendMessage(tlJoin("infoDependencies", plugin.getDescription().getDepend()));
            }

            if (!plugin.getDescription().getSoftDepend().isEmpty()) {
                sender.sendMessage(tlJoin("infoSoftDependencies", plugin.getDescription().getSoftDepend()));
            }

            sender.sendMessage(tl("infoStatus", plugin.isEnabled() ? tl("enabled") : tl("disabled")));
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

            Collections.sort(completion);

            return completion;
        }

        return Collections.emptyList();
    }
}
