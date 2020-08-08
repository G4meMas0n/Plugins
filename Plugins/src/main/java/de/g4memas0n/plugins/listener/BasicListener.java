package de.g4memas0n.plugins.listener;

import de.g4memas0n.plugins.Plugins;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Representation of a event listener.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public abstract class BasicListener implements Listener {

    private Plugins instance;

    protected BasicListener() { }

    public final void register(@NotNull final Plugins instance) {
        if (this.instance != null) {
            return;
        }

        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    public final void unregister() {
        if (this.instance == null) {
            return;
        }

        HandlerList.unregisterAll(this);

        this.instance = null;
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "{events=" + String.join(",", this.getEvents()) + "}";
    }

    protected final @NotNull Plugins getInstance() {
        if (this.instance == null) {
            throw new IllegalStateException("Unregistered listener '" + this.getClass().getSimpleName()
                    + "' tried to get the plugin instance");
        }

        return this.instance;
    }

    protected final @NotNull List<String> getEvents() {
        final List<String> events = new ArrayList<>();

        for (final Method method : this.getClass().getMethods()) {
            if (method.getAnnotation(EventHandler.class) != null) {
                if (method.getParameterCount() != 1) {
                    continue;
                }

                final Class<?> clazz = method.getParameterTypes()[0];

                if (clazz.isAssignableFrom(Event.class) && !events.contains(clazz.getSimpleName())) {
                    events.add(clazz.getSimpleName());
                }
            }
        }

        return events;
    }
}
