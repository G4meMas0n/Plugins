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
 * The enable command that allows to enable plugins.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class EnableCommand extends BasicCommand {

    private static final int PLUGIN = 0;

    public EnableCommand() {
        super("enable", 1, 1);

        this.setPermission("plugins.enable");
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
                sender.sendMessage(tlErr("enableSelf"));
                return true;
            }

            for (final String name : plugin.getDescription().getDepend()) {
                final Plugin dependency = this.getInstance().getServer().getPluginManager().getPlugin(name);

                if (dependency == null || !dependency.isEnabled()) { // dependency == null should never be the case.
                    sender.sendMessage(tl("enableDependency", plugin.getName(), name));
                    return true;
                }
            }

            for (final String name : plugin.getDescription().getSoftDepend()) {
                final Plugin dependency = this.getInstance().getServer().getPluginManager().getPlugin(name);

                if (dependency != null && !dependency.isEnabled()) {
                    sender.sendMessage(tl("enableDependency", plugin.getName(), name));
                    return true;
                }
            }

            if (!plugin.isEnabled()) {
                this.getInstance().getLogger().info(String.format("Got task to enable plugin: Enabling plugin '%s'...", plugin.getName()));
                this.getInstance().getServer().getPluginManager().enablePlugin(plugin);
                this.getInstance().getLogger().info(String.format("Please make sure that plugin '%s' was enabled correctly.", plugin.getName()));

                sender.sendMessage(tl("enablePlugin", plugin.getName()));
                return true;
            }

            sender.sendMessage(tl("enableAlready", plugin.getName()));
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
                if (plugin.isEnabled()) {
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
