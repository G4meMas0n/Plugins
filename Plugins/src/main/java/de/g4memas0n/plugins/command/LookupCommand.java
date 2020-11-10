package de.g4memas0n.plugins.command;

import de.g4memas0n.plugins.util.Registration;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.g4memas0n.plugins.util.Messages.tl;
import static de.g4memas0n.plugins.util.Messages.tlErr;

/**
 * The lookup command that allows to lookup for plugins that has registered a specified command or alias.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class LookupCommand extends BasicCommand {

    private static final String ALIASES_KEY = "aliases";

    private static final int COMMAND = 0;

    public LookupCommand() {
        super("lookup", 1, 1);

        this.setPermission("plugins.lookup");
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender,
                           @NotNull final String[] arguments) {
        if (this.argsInRange(arguments.length)) {
            final PluginCommand command = this.getInstance().getServer().getPluginCommand(arguments[COMMAND]);

            if (command == null) {
                sender.sendMessage(tlErr("commandNotFound", arguments[COMMAND]));
                return true;
            }

            final Set<Registration> registrations = new HashSet<>();

            for (final Plugin plugin : this.getInstance().getServer().getPluginManager().getPlugins()) {
                if (plugin.equals(command.getPlugin())) {
                    continue;
                }

                for (final String cmd : plugin.getDescription().getCommands().keySet()) {
                    if (cmd.equalsIgnoreCase(arguments[COMMAND])) {
                        registrations.add(new Registration(plugin));
                    }

                    final Map<String, Object> description = plugin.getDescription().getCommands().get(cmd);

                    if (description.containsKey(ALIASES_KEY)) {
                        if (description.get(ALIASES_KEY) instanceof String) {
                            if (((String) description.get(ALIASES_KEY)).equalsIgnoreCase(arguments[COMMAND])) {
                                registrations.add(new Registration(plugin, cmd));
                            }
                        } else if (description.get(ALIASES_KEY) instanceof List) {
                            for (final Object alias : (List<?>) description.get(ALIASES_KEY)) {
                                if (alias instanceof String && ((String) alias).equalsIgnoreCase(arguments[COMMAND])) {
                                    registrations.add(new Registration(plugin, cmd));
                                }
                            }
                        }
                    }
                }
            }

            sender.sendMessage(tl("lookupHeader", arguments[COMMAND].toLowerCase()));
            sender.sendMessage(tl("lookupType", command.getName().equalsIgnoreCase(arguments[COMMAND])
                    ? tl("command") : tl("alias", command.getName())));
            sender.sendMessage(tl("lookupOwner", command.getPlugin().getName()));

            if (!registrations.isEmpty()) {
                sender.sendMessage(tl("lookupOtherHeader"));

                registrations.stream().sorted().forEach(result -> sender.sendMessage(tl("lookupOtherEntry",
                        result.getLabel() == null ? tl("command") : tl("alias", result.getLabel()),
                        result.getPlugin().getName())));
            }

            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender sender,
                                             @NotNull final String[] arguments) {
        if (arguments.length == COMMAND + 1) {
            final Set<String> completions = new HashSet<>();

            for (final Plugin plugin : this.getInstance().getServer().getPluginManager().getPlugins()) {
                for (final String command : plugin.getDescription().getCommands().keySet()) {
                    if (StringUtil.startsWithIgnoreCase(command, arguments[COMMAND])) {
                        completions.add(command);
                    }
                }

                for (final Map<String, Object> command : plugin.getDescription().getCommands().values()) {
                    if (!command.containsKey(ALIASES_KEY)) {
                        continue;
                    }

                    if (command.get(ALIASES_KEY) instanceof String) {
                        if (StringUtil.startsWithIgnoreCase((String) command.get(ALIASES_KEY), arguments[COMMAND])) {
                            completions.add((String) command.get(ALIASES_KEY));
                        }

                        continue;
                    }

                    if (command.get(ALIASES_KEY) instanceof List) {
                        for (final Object alias : (List<?>) command.get(ALIASES_KEY)) {
                            if (alias instanceof String) {
                                if (StringUtil.startsWithIgnoreCase((String) alias, arguments[COMMAND])) {
                                    completions.add((String) alias);
                                }
                            }
                        }
                    }
                }
            }

            return completions.stream().sorted().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
