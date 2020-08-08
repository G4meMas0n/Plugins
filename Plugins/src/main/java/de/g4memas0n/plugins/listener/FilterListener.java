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
            for (final Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext();) {
                final Matcher match = this.regex.matcher(iterator.next());

                if (match.matches() || this.getInstance().getSettings().isFilterCommand(match.group())) {
                    iterator.remove();
                }
            }

            this.getInstance().getLogger().debug(String.format("Filtered tab-completion commands for player: %s", event.getPlayer().getName()));
            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            for (final Iterator<String> iterator = event.getCommands().iterator(); iterator.hasNext();) {
                final Matcher match = this.regex.matcher(iterator.next());

                if (this.getInstance().getSettings().isFilterCommand(match.matches()
                        ? match.group("command") : match.group())) {
                    iterator.remove();
                }
            }

            this.getInstance().getLogger().debug(String.format("Filtered tab-completion commands for player: %s", event.getPlayer().getName()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(@NotNull final PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("plugins.filter.exempt")) {
            return;
        }

        if (this.getInstance().getSettings().isFilterNamespaces()) {
            final Matcher match = this.regex.matcher(event.getMessage().split(" ")[0]);

            if (match.matches() || this.getInstance().getSettings().getFilterCommands().contains(match.group())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                this.getInstance().getLogger().debug(String.format("Blocked execution of filtered command for player: %s", event.getPlayer().getName()));
            }

            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            final Matcher match = this.regex.matcher(event.getMessage().split(" ")[0]);

            if (this.getInstance().getSettings().isFilterCommand(match.matches()
                    ? match.group("command") : match.group())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.getInstance().getSettings().getFilterMessage());

                this.getInstance().getLogger().debug(String.format("Blocked execution of filtered command for player: %s", event.getPlayer().getName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSenderTabCompleteEvent(@NotNull final TabCompleteEvent event) {
        if (event.getSender().hasPermission("plugins.filter.exempt")) {
            return;
        }

        if (this.getInstance().getSettings().isFilterNamespaces()) {
            final Matcher match = this.regex.matcher(event.getBuffer().split(" ")[0]);

            if (match.matches() || this.getInstance().getSettings().isFilterCommand(match.group())) {
                event.setCompletions(Collections.emptyList());

                this.getInstance().getLogger().debug(String.format("Blocked tab-completion of filtered command for sender: %s", event.getSender().getName()));
            }

            return;
        }

        if (!this.getInstance().getSettings().getFilterCommands().isEmpty()) {
            final Matcher match = this.regex.matcher(event.getBuffer().split(" ")[0]);

            if (this.getInstance().getSettings().isFilterCommand(match.matches()
                    ? match.group("command") : match.group())) {
                event.setCompletions(Collections.emptyList());

                this.getInstance().getLogger().debug(String.format("Blocked tab-completion of filtered command for sender: %s", event.getSender().getName()));
            }
        }
    }
}
