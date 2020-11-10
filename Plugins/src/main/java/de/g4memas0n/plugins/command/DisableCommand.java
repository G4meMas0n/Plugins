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

/**
 * The disable command that allows to disable plugins.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class DisableCommand extends BasicCommand {

    private static final int PLUGIN = 0;

    public DisableCommand() {
        super("disable", 1, 1);

        this.setPermission("plugins.disable");
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

            if (plugin.equals(this.getInstance())) {
                sender.sendMessage(tlErr("disableSelf"));
                return true;
            }

            for (final Plugin depend : this.getInstance().getServer().getPluginManager().getPlugins()) {
                if (depend.getDescription().getSoftDepend().contains(plugin.getName())
                        || depend.getDescription().getDepend().contains(plugin.getName())) {
                    sender.sendMessage(tl("disableDependency", plugin.getName(), depend.getName()));
                    return true;
                }
            }

            if (plugin.isEnabled()) {
                this.getInstance().getLogger().info(String.format("Got task to disable plugin: Disabling plugin '%s'...", plugin.getName()));
                this.getInstance().getServer().getPluginManager().disablePlugin(plugin);
                this.getInstance().getLogger().info(String.format("Please make sure that plugin '%s' was disabled correctly.", plugin.getName()));

                sender.sendMessage(tl("disablePlugin", plugin.getName()));
                return true;
            }

            sender.sendMessage(tl("disableAlready", plugin.getName()));
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
                if (!plugin.isEnabled()) {
                    continue;
                }

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
