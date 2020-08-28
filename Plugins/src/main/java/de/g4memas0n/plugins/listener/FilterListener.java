package de.g4memas0n.plugins.listener;

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

    protected final Pattern regex = Pattern.compile("^/?(?<namespace>\\w+):(?<command>.+)$");

    public FilterListener() { }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSendEvent(@NotNull final PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission("plugins.filter.exempt")) {
            return;
        }

        if (this.getInstance().getSettings().isFilterNamespaces()) {
            try {
                if (event.getCommands().removeIf(command -> this.regex.matcher(command).matches() || this.getInstance().getSettings().isFilterCommand(command))) {
                    this.getInstance().getLogger().debug(String.format("Filtered tab-completion commands for player: %s", event.getPlayer().getName()));
                }
            } catch (UnsupportedOperationException ex) {
                this.getInstance().getLogger().warning(String.format("Unable to filter tab-completion commands for player '%s' due to custom command-send event.", event.getPlayer().getName()));
            }

            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            try {
                String command;
                Matcher match;

                for (final Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext();) {
                    match = this.regex.matcher(command = iterator.next());

                    if (this.getInstance().getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                        iterator.remove();
                    }
                }

                this.getInstance().getLogger().debug(String.format("Filtered tab-completion commands for player: %s", event.getPlayer().getName()));
            } catch (UnsupportedOperationException ex) {
                this.getInstance().getLogger().warning(String.format("Unable to filter tab-completion commands for player '%s' due to custom command-send event.", event.getPlayer().getName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(@NotNull final PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("plugins.filter.exempt")) {
            return;
        }

        if (this.getInstance().getSettings().isFilterNamespaces()) {
            final String command = event.getMessage().split(" ")[0];

            if (this.regex.matcher(command).matches()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                this.getInstance().getLogger().debug(String.format("Blocked execution of namespace command '%s' for player: %s", command, event.getPlayer().getName()));
                return;
            }

            if (this.getInstance().getSettings().isFilterCommand(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                this.getInstance().getLogger().debug(String.format("Blocked execution of filtered command '%s' for player: %s", command, event.getPlayer().getName()));
            }

            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            final String command = event.getMessage().split(" ")[0];
            final Matcher match = this.regex.matcher(command);

            if (this.getInstance().getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                this.getInstance().getLogger().debug(String.format("Blocked execution of filtered command '%s' for player: %s", command, event.getPlayer().getName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSenderTabCompleteEvent(@NotNull final TabCompleteEvent event) {
        if (event.getSender().hasPermission("plugins.filter.exempt")) {
            return;
        }

        if (this.getInstance().getSettings().isFilterNamespaces()) {
            final String command = event.getBuffer().split(" ")[0];

            if (this.regex.matcher(command).matches()) {
                event.setCompletions(Collections.emptyList());

                this.getInstance().getLogger().debug(String.format("Blocked tab-completion of namespace command '%s' for player: %s", command, event.getSender().getName()));
                return;
            }

            if (this.getInstance().getSettings().isFilterCommand(command)) {
                event.setCompletions(Collections.emptyList());

                this.getInstance().getLogger().debug(String.format("Blocked tab-completion of filtered command '%s' for player: %s", command, event.getSender().getName()));
            }

            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            final String command = event.getBuffer().split(" ")[0];
            final Matcher match = this.regex.matcher(command);

            if (this.getInstance().getSettings().isFilterCommand(match.matches() ? match.group("command") : command)) {
                event.setCompletions(Collections.emptyList());

                this.getInstance().getLogger().debug(String.format("Blocked tab-completion of filtered command '%s' for sender: %s", command, event.getSender().getName()));
            }
        }
    }
}
