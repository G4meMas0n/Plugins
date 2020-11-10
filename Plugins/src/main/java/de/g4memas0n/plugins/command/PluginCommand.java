package de.g4memas0n.plugins.command;

import de.g4memas0n.plugins.Plugins;
import de.g4memas0n.plugins.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main plugin command that delegates to all sub-commands.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class PluginCommand extends BasicPluginCommand {

    private static final int DELEGATE = 0;
    private static final int ARGUMENTS = 1;

    private final Map<String, BasicCommand> commands = new HashMap<>(8, 1);

    public PluginCommand() {
        super("plugin", 1, -1);

        this.addCommand(new DisableCommand());
        this.addCommand(new EnableCommand());
        this.addCommand(new InfoCommand());
        this.addCommand(new LoadCommand());
        this.addCommand(new LookupCommand());
        this.addCommand(new ReloadCommand());
        this.addCommand(new VersionCommand());

        this.setPermission("plugins.manage");
    }

    public final @Nullable BasicCommand getCommand(@NotNull final String name) {
        final BasicCommand delegate = this.commands.get(name.toLowerCase());

        if (delegate != null) {
            return delegate;
        }

        for (final BasicCommand command : this.commands.values()) {
            for (final String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return command;
                }
            }
        }

        return null;
    }

    public final void addCommand(@NotNull final BasicCommand command) {
        if (this.commands.containsKey(command.getName())) {
            return;
        }

        this.commands.put(command.getName(), command);
    }

    @SuppressWarnings("unused")
    public final void removeCommand(@NotNull final BasicCommand command) {
        if (!this.commands.containsKey(command.getName())) {
            return;
        }

        this.commands.remove(command.getName(), command);

        command.unregister();
    }

    @Override
    public boolean register(@NotNull final Plugins instance) {
        if (super.register(instance)) {
            this.commands.values().forEach(command -> command.register(instance));
            return true;
        }

        return false;
    }

    @Override
    public boolean unregister() {
        if (super.unregister()) {
            this.commands.values().forEach(BasicCommand::unregister);
            return true;
        }

        return false;
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender,
                           @NotNull final String[] arguments) {
        if (this.argsInRange(arguments.length)) {
            final BasicCommand delegate = this.getCommand(arguments[DELEGATE]);

            if (delegate == null) {
                sender.sendMessage(Messages.tlErr("commandNotFound", arguments[DELEGATE]));
                return true;
            }

            if (sender.hasPermission(delegate.getPermission())) {
                if (delegate.execute(sender, arguments.length <= ARGUMENTS ? new String[0]
                        : Arrays.copyOfRange(arguments, ARGUMENTS, arguments.length))) {
                    return true;
                }

                // Invalid command usage. Send syntax help:
                sender.sendMessage(delegate.getDescription());
                sender.sendMessage(delegate.getUsage());
                return true;
            }

            sender.sendMessage(Messages.tl("noPermission"));
            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender,
                                             @NotNull final String[] arguments) {
        if (arguments.length == DELEGATE + 1) {
            final List<String> completion = new ArrayList<>();

            for (final BasicCommand delegate : this.commands.values()) {
                if (!sender.hasPermission(delegate.getPermission())) {
                    continue;
                }

                if (StringUtil.startsWithIgnoreCase(delegate.getName(), arguments[DELEGATE])) {
                    completion.add(delegate.getName());
                }

                for (final String alias : delegate.getAliases()) {
                    if (StringUtil.startsWithIgnoreCase(alias, arguments[DELEGATE])) {
                        completion.add(alias);
                    }
                }
            }

            Collections.sort(completion);

            return completion;
        }

        if (arguments.length > ARGUMENTS) {
            final BasicCommand delegate = this.getCommand(arguments[DELEGATE]);

            if (delegate == null) {
                return Collections.emptyList();
            }

            if (sender.hasPermission(delegate.getPermission())) {
                return delegate.tabComplete(sender, Arrays.copyOfRange(arguments, ARGUMENTS, arguments.length));
            }
        }

        return Collections.emptyList();
    }
}
