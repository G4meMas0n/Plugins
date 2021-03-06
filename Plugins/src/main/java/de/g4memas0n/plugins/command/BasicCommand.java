package de.g4memas0n.plugins.command;

import de.g4memas0n.plugins.Plugins;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Abstract Command Representation that represents all non bukkit/spigot commands.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public abstract class BasicCommand {

    private final String name;
    private final int minArgs;
    private final int maxArgs;

    private Plugins instance;
    private String permission;

    protected BasicCommand(@NotNull final String name,
                           final int minArgs,
                           final int maxArgs) {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.permission = "";
    }

    public boolean register(@NotNull final Plugins instance) {
        if (this.instance != null) {
            return false;
        }

        this.instance = instance;

        if (this.instance.getSettings().isDebug()) {
            this.instance.getLogger().info("Registered command: " + this.toString());
        }

        return true;
    }

    public boolean unregister() {
        if (this.instance == null) {
            return false;
        }

        if (this.instance.getSettings().isDebug()) {
            this.instance.getLogger().info("Unregistered command: " + this.toString());
        }

        this.instance = null;
        return true;
    }

    public final @NotNull Plugins getInstance() {
        if (this.instance == null) {
            throw new IllegalStateException("Unregistered command '" + this.name + "' tried to get the plugin instance");
        }

        return this.instance;
    }

    public final @NotNull String getName() {
        return this.name;
    }

    public final int getMinArgs() {
        return this.minArgs;
    }

    @SuppressWarnings("unused")
    public final int getMaxArgs() {
        return this.maxArgs;
    }

    public final boolean argsInRange(final int arguments) {
        return this.maxArgs > 0
                ? arguments >= this.minArgs && arguments <= this.maxArgs
                : arguments >= this.minArgs;
    }

    /**
     * Executes the command for the given sender, returning its success.
     *
     * <p>If false is returned, then the help of the command will be sent to the sender.</p>
     *
     * @param sender the source who executed the command.
     * @param arguments the passed arguments of the sender.
     * @return true if the command execution was valid, false otherwise.
     */
    public abstract boolean execute(@NotNull final CommandSender sender,
                                    @NotNull final String[] arguments);

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender the source who tab-completed the command.
     * @param arguments the passed arguments of the sender, including the final partial argument to be completed.
     * @return a list of possible completions for the final arguments.
     */
    public abstract @NotNull List<String> tabComplete(@NotNull final CommandSender sender,
                                                      @NotNull final String[] arguments);

    public @NotNull String getPermission() {
        return this.permission;
    }

    public void setPermission(@NotNull final String permission) {
        if (permission.equals(this.permission)) {
            return;
        }

        this.permission = permission;
    }

    public final @NotNull String getDescription() {
        return this.instance.getMessages().translate(this.name.concat("CommandDescription"));
    }

    public final @NotNull String getUsage() {
        return this.instance.getMessages().translate(this.name.concat("CommandUsage"));
    }

    @Override
    public final @NotNull String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());

        builder.append("{name=");
        builder.append(this.name);
        builder.append(";min-args=");
        builder.append(this.minArgs);
        builder.append(";max-args=");
        builder.append(this.maxArgs);

        if (!this.permission.isEmpty()) {
            builder.append(";permission=");
            builder.append(this.permission);
        }

        return builder.append("}").toString();
    }

    @Override
    public final boolean equals(@Nullable final Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (object instanceof BasicCommand) {
            final BasicCommand other = (BasicCommand) object;

            return this.name.equals(other.name)
                    && this.minArgs == other.minArgs
                    && this.maxArgs == other.maxArgs;
        }

        return false;
    }

    @Override
    public final int hashCode() {
        final int prime = 69;
        int result = 2;

        result = prime * result + this.name.hashCode();
        result = prime * result + Integer.hashCode(this.minArgs);
        result = prime * result + Integer.hashCode(this.maxArgs);

        return result;
    }
}
