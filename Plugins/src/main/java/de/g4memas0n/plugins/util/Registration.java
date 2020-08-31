package de.g4memas0n.plugins.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a registration of an alias or a command of a plugin.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public class Registration implements Comparable<Registration> {

    private final Plugin plugin;
    private final String label;

    public Registration(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.label = null;
    }

    public Registration(@NotNull final Plugin plugin,
                         @NotNull final String label) {
        this.plugin = plugin;
        this.label = label;
    }

    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    public @Nullable String getLabel() {
        return this.label;
    }

    @Override
    public int compareTo(@NotNull final Registration other) {
        if (this.plugin.equals(other.plugin)) {
            if (this.label != null) {
                return other.label == null ? 1 : this.label.compareToIgnoreCase(other.label);
            }

            return other.label == null ? 0 : -1;
        }

        return this.plugin.getName().compareToIgnoreCase(other.plugin.getName());
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (object instanceof Registration) {
            final Registration other = (Registration) object;

            if (this.plugin.equals(other.plugin)) {
                return this.label != null ? this.label.equalsIgnoreCase(other.label) : other.label == null;
            }

            return false;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.plugin.getName().hashCode(); // Sorts registrations of the same plugin into the same bucket.
    }
}
