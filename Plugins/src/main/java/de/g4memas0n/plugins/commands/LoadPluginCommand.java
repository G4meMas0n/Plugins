package de.g4memas0n.plugins.commands;

import de.g4memas0n.plugins.PluginManager;
import de.g4memas0n.plugins.manager.Messages;
import de.g4memas0n.plugins.PluginManager$Command;
import de.g4memas0n.plugins.manager.FileManager;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class LoadPluginCommand implements CommandExecutor, TabCompleter {
    private static final int FILE_ARG = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {
        if (sender instanceof BlockCommandSender) {
            PluginManager.logToConsole(Level.SEVERE, Messages.COMMAND_FROM_CONSOLE);
            return false;
        }

        if (sender.hasPermission(PluginManager$Command.LOAD_PLUGIN.getPermission())) {
            if (args.length == FILE_ARG) {
                String fileName = args[FILE_ARG - 1];
                String pluginName = "";

                try {
                    File pluginFile = FileManager.getPluginArchive(fileName);
                    PluginDescriptionFile descriptionFile = FileManager.getDescriptionFile(pluginFile);

                    if (pluginFile != null && descriptionFile != null) {
                        pluginName = descriptionFile.getName();

                        if (isAlreadyLoaded(descriptionFile)) {
                            PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE,
                                    pluginName, "Plugin already Loaded."));

                            sender.sendMessage(Messages.tl(true, "load_error_alreadyLoaded",
                                    descriptionFile.getName()));

                        } else if (!areDependenciesEnabled(descriptionFile.getDepend())) {
                            PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE,
                                    pluginName, "Dependencies already enabled."));

                            sender.sendMessage(Messages.tl(true, "load_error_dependenciesEnabled",
                                    String.join(", ", descriptionFile.getDepend())));

                        } else if (!areSoftDependenciesEnabled(descriptionFile.getSoftDepend())) {
                            PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE,
                                    pluginName, "Soft Dependencies already enabled."));

                            sender.sendMessage(Messages.tl(true, "load_error_softDependenciesEnabled",
                                    String.join(", ", descriptionFile.getSoftDepend())));

                        } else if (!isLoadBefore(descriptionFile.getLoadBefore())) {
                            PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE,
                                    pluginName, String.format("Plugin should be loaded before %s.",
                                            String.join(", ", descriptionFile.getLoadBefore()))));

                            sender.sendMessage(Messages.tl(true, "load_error_loadBeforeLoaded",
                                    String.join(", ", descriptionFile.getLoadBefore())));

                        } else {
                            PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_LOADING, pluginName,
                                    fileName));

                            Plugin plugin = PluginManager.getInstance().getPluginManager().loadPlugin(pluginFile);
                            if (plugin != null) {
                                plugin.onLoad();

                                PluginManager.logToConsole(Level.INFO, String.format(Messages.PLUGIN_LOAD_SUCCESS,
                                        pluginName));

                                sender.sendMessage(Messages.tl(false, "load_success", plugin.getName()));
                            }
                        }
                    } else {
                        sender.sendMessage(Messages.tl(true, "load_error_unexpected"));
                    }
                } catch (FileSystemNotFoundException ex) {
                    PluginManager.logToConsole(Level.WARNING, String.format(Messages.FILE_LOAD_FAILURE, fileName,
                            ex.getMessage()));
                    //PluginManager.logStackTrace(ex);

                    sender.sendMessage(Messages.tl(true, "load_error_noFile", fileName));
                } catch (IllegalArgumentException ex) {
                    PluginManager.logToConsole(Level.WARNING, String.format(Messages.FILE_LOAD_FAILURE, fileName,
                            ex.getMessage()));
                    //PluginManager.logStackTrace(ex);

                    sender.sendMessage(Messages.tl(true, "load_error_invalidFile"));
                } catch (InvalidPluginException ex) {
                    PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE, pluginName,
                            ex.getMessage()));
                    PluginManager.logStackTrace(ex);

                    sender.sendMessage(Messages.tl(true, "load_error_invalidPlugin"));
                } catch (InvalidDescriptionException ex) {
                    PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE, pluginName,
                            ex.getMessage()));
                    PluginManager.logStackTrace(ex);

                    sender.sendMessage(Messages.tl(true, "load_error_invalidDescription"));
                } catch (UnknownDependencyException ex) {
                    PluginManager.logToConsole(Level.WARNING, String.format(Messages.PLUGIN_LOAD_FAILURE, pluginName,
                            ex.getMessage()));
                    PluginManager.logStackTrace(ex);

                    sender.sendMessage(Messages.tl(true, "load_error_unknownDependency"));
                }

                return true;
            }

            return false;
        } else {
            // The command sender is not permitted to use this command.
            sender.sendMessage(Messages.tl(false, "global_noPermission"));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      @NotNull String[] args) {
        List<String> availableFiles = new ArrayList<>();
        File[] dirFiles = FileManager.getPluginsFolder().listFiles();

        if (dirFiles != null) {
            if (args.length == FILE_ARG) {
                // Searches for all Files in the plugins folder and put the name of them into the availableFiles list.
                for (File current : dirFiles) {
                    if (current.isFile()) {
                        if (current.getName().endsWith(FileManager.getJarArchiveFormat())) {
                            if (current.getName().contains(args[FILE_ARG - 1])) {
                                availableFiles.add(current.getName());
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(availableFiles);
        return availableFiles;
    }

    private boolean isAlreadyLoaded(@NotNull PluginDescriptionFile descriptionFile) {
        for (Plugin current : PluginManager.getInstance().getPluginManager().getPlugins()) {
            if (current.getDescription().getMain().equals(descriptionFile.getMain())) {
                return true;
            }
        }

        return false;
    }

    private boolean areDependenciesEnabled(@NotNull List<String> dependencies) throws UnknownDependencyException {
        if (!dependencies.isEmpty()) {
            for (String current : dependencies) {
                Plugin currentPlugin = PluginManager.getInstance().getPluginManager().getPlugin(current);

                if (currentPlugin == null) {
                    throw new UnknownDependencyException(String.format("Unknown Dependency: %s", current));
                }

                if (!currentPlugin.isEnabled()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean areSoftDependenciesEnabled(@NotNull List<String> softDependencies) {
        if (!softDependencies.isEmpty()) {
            for (String current : softDependencies) {
                Plugin currentPlugin = PluginManager.getInstance().getPluginManager().getPlugin(current);

                if (currentPlugin == null || !currentPlugin.isEnabled()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isLoadBefore(@NotNull List<String> loadBefore) {
        if (!loadBefore.isEmpty()) {
            for (String current : loadBefore) {
                if (PluginManager.getInstance().getPluginManager().getPlugin(current) != null) {
                    return false;
                }
            }
        }

        return true;
    }
}