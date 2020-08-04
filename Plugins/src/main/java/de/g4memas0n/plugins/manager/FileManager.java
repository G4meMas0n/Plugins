package de.g4memas0n.plugins.manager;

import de.g4memas0n.plugins.PluginManager;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

public class FileManager {
    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String DESCRIPTION_FILE_NAME = "plugin.yml";
    private static final String JAR_ARCHIVE_FORMAT = ".jar";

    @NotNull
    public static File getPluginsFolder() {
        return new File(PluginManager.getInstance().getDataFolder(), "..");
    }

    @NotNull
    public static File getConfigFile() {
        return new File(PluginManager.getInstance().getDataFolder(), CONFIG_FILE_NAME);
    }

    @NotNull
    public static String getJarArchiveFormat() {
        return JAR_ARCHIVE_FORMAT;
    }

    @Nullable
    public static PluginDescriptionFile getDescriptionFile(@Nullable File pluginFile) throws InvalidPluginException,
            InvalidDescriptionException {
        if (pluginFile != null && pluginFile.exists()) {
            try {
                JarFile pluginJarFile = new JarFile(pluginFile);
                ZipEntry pluginYML = pluginJarFile.getEntry(DESCRIPTION_FILE_NAME);

                if (pluginYML != null) {
                    PluginDescriptionFile descriptionFile = new PluginDescriptionFile(pluginJarFile.getInputStream(pluginYML));
                    pluginJarFile.close();
                    return descriptionFile;
                } else {
                    pluginJarFile.close();
                    throw new InvalidPluginException("Java Archive contains no Plugin Description File");
                }
            } catch (IOException | SecurityException ex) {
                PluginManager.logToConsole(Level.WARNING, String.format(Messages.DESCRIPTION_FILE_LOAD_FAILURE,
                        ex.getMessage()));
                PluginManager.logStackTrace(ex);
            }
        }

        return null;
    }

    @Nullable
    public static File getPluginArchive(@NotNull String archiveName) throws FileSystemNotFoundException,
            IllegalArgumentException {
        File pluginFile = getFile(getPluginsFolder(), archiveName);

        if (pluginFile != null) {
            if (pluginFile.exists()) {
                if (pluginFile.getName().endsWith(JAR_ARCHIVE_FORMAT)) {
                    return pluginFile;
                } else {
                    throw new IllegalArgumentException("Archive Format mismatch. Expected: JAR_ARCHIVE");
                }
            } else {
                throw new FileSystemNotFoundException(String.format("File not found: %s", pluginFile.getName()));
            }
        } else {
            return null;
        }
    }

    @Nullable
    private static File getFile(@NotNull File folder, @NotNull String fileName) throws FileSystemNotFoundException {
        try {
            File outputFile = Paths.get(new File(folder, fileName).toURI()).toFile();

            PluginManager.logToConsole(Level.INFO, String.format(Messages.FILE_LOAD, fileName));

            return outputFile;
        } catch (IllegalArgumentException | SecurityException ex) {
            PluginManager.logToConsole(Level.WARNING, String.format(Messages.FILE_LOAD_FAILURE, fileName,
                    ex.getMessage()));
            PluginManager.logStackTrace(ex);

            return null;
        }
    }
}
