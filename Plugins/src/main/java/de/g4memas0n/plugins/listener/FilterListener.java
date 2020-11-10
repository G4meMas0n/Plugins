package de.g4memas0n.plugins.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Filter Listener, listening for pre command execution and tab-completion events.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public final class FilterListener extends BasicListener {

    private final Pattern regex = Pattern.compile("^/?(\\w+):(?<command>/?.+)$");

    public FilterListener() { }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSendEvent(@NotNull final PlayerCommandSendEvent event) {
        // Check if one of the filtering mechanics is enabled.
        if (this.getSettings().isFilterNamespaces() || this.getSettings().isFilterCommands()) {
            // If true, return if the player is permitted to bypass command filtering.
            if (event.getPlayer().hasPermission("plugins.filter.exempt")) {
                return;
            }

            // Check if the filter namespace mechanic is enabled.
            if (this.getSettings().isFilterNamespaces()) {
                try {
                    // If true, check if namespaces got removed.
                    if (event.getCommands().removeIf(command -> this.regex.matcher(command).matches())) {
                        if (this.getSettings().isDebug()) {
                            this.getLogger().info("Filtered name-spaced commands for player: " + event.getPlayer().getName());
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                    this.getLogger().warning(String.format("Unable to filter name-spaced commands for player '%s' due to custom command-send event.", event.getPlayer().getName()));
                }
            }

            // Check if the filter command mechanic is enabled.
            if (this.getSettings().isFilterCommands()) {
                try {
                    final int size = event.getCommands().size();

                    String command;
                    Matcher match;

                    // If true, iterate over all commands and remove filtered commands.
                    for (final Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext();) {
                        match = this.regex.matcher(command = iterator.next());

                        if (this.getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                            iterator.remove();
                        }
                    }

                    if (event.getCommands().size() < size && this.getSettings().isDebug()) {
                        this.getLogger().info(String.format("Filtered tab-completion commands for player: %s", event.getPlayer().getName()));
                    }
                } catch (UnsupportedOperationException ex) {
                    this.getLogger().warning(String.format("Unable to filter tab-completion commands for player '%s' due to custom command-send event.", event.getPlayer().getName()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(@NotNull final PlayerCommandPreprocessEvent event) {
        // Check if one of the filtering mechanics is enabled.
        if (this.getSettings().isFilterNamespaces() || this.getSettings().isFilterCommands()) {
            // If true, return if the player is permitted to bypass command filtering.
            if (event.getPlayer().hasPermission("plugins.filter.exempt")) {
                return;
            }

            final String command = event.getMessage().split(" ")[0];

            // Check if the filter namespace mechanic is enabled.
            if (this.getSettings().isFilterNamespaces()) {
                // If true, check if processed command is a name-spaced command.
                if (this.regex.matcher(command).matches()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                    if (this.getSettings().isDebug()) {
                        this.getLogger().info(String.format("Blocked execution of namespace command '%s' for player: %s", command, event.getPlayer().getName()));
                    }

                    return;
                }
            }

            // Check if the filter command mechanic is enabled.
            if (this.getSettings().isFilterCommands()) {
                final Matcher match = this.regex.matcher(command);

                // If true, check if processed command is a filtered command.
                if (this.getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                    if (this.getSettings().isDebug()) {
                        this.getLogger().info(String.format("Blocked execution of filtered command '%s' for player: %s", command, event.getPlayer().getName()));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSenderTabCompleteEvent(@NotNull final TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        // Check if one of the filtering mechanics is enabled.
        if (this.getSettings().isFilterNamespaces() || this.getSettings().isFilterCommands()) {
            // If true, return if the player is permitted to bypass command filtering.
            if (event.getSender().hasPermission("plugins.filter.exempt")) {
                return;
            }

            final String command = event.getBuffer().split(" ")[0];

            // Check if the filter namespace mechanic is enabled.
            if (this.getSettings().isFilterNamespaces()) {
                if (this.regex.matcher(command).matches()) {
                    event.setCompletions(Collections.emptyList());

                    if (this.getSettings().isDebug()) {
                        this.getLogger().info(String.format("Blocked tab-completion of namespace command '%s' for player: %s", command, event.getSender().getName()));
                    }

                    return;
                }
            }

            // Check if the filter command mechanic is enabled.
            if (this.getSettings().isFilterCommands()) {
                final Matcher match = this.regex.matcher(command);

                if (this.getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                    event.setCompletions(Collections.emptyList());

                    if (this.getSettings().isDebug()) {
                        this.getLogger().info(String.format("Blocked tab-completion of filtered command '%s' for sender: %s", command, event.getSender().getName()));
                    }
                }
            }
        }
    }
}
