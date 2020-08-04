package de.g4memas0n.plugins;

import org.jetbrains.annotations.NotNull;

public enum PluginManager$Permission {
    DISABLE_PLUGIN(".disablePlugin"),
    ENABLE_PLUGIN(".enablePlugin"),
    LOAD_PLUGIN(".loadPlugin"),
    MAIN_VERSION(".version"),
    MAIN_RELOAD(".reload"),
    MAIN(".main", MAIN_VERSION, MAIN_RELOAD);

    private String permissionPrefix;
    private String permissionMiddle;
    private String permissionSuffix;

    PluginManager$Permission(@NotNull String permissionSuffix) {
        this.permissionPrefix = PluginManager.getInstance().getDescription().getName();
        this.permissionSuffix = permissionSuffix.toLowerCase();
    }

    PluginManager$Permission(@NotNull String permissionSuffix, @NotNull PluginManager$Permission... childrenPerms) {
        this.permissionPrefix = PluginManager.getInstance().getDescription().getName();
        this.permissionSuffix = permissionSuffix.toLowerCase();

        for (PluginManager$Permission current : childrenPerms) {
            current.permissionMiddle = this.permissionSuffix;
        }
    }

    @NotNull
    public String getSuffix() {
        return this.permissionSuffix;
    }

    @NotNull
    public String toString() {
        return this.permissionMiddle != null ? this.permissionPrefix + this.permissionMiddle + this.permissionSuffix :
                this.permissionPrefix + this.permissionSuffix;
    }
}
