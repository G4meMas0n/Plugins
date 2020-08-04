package de.g4memas0n.plugins.manager;

import de.g4memas0n.plugins.PluginManager;
import de.g4memas0n.plugins.PluginManager$Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class Messages {
    // Public Message Strings:
    public static final String CHECK_STACK_TRACE = "Please check Stack Trace below:";
    public static final String COMMAND_FROM_CONSOLE = "Trying to perform command %s from CommandBlock. This is not allowed!";
    public static final String COMMAND_INIT_FAILURE = "Unable to register Commands. %s";
    public static final String CONFIG_LOAD_FAILURE = "Unable to load 'config.yml'. %s";
    public static final String CONFIG_USE_PREVIOUS = "Using previous config...";
    public static final String FILE_LOAD_FAILURE = "Unable to load File %s: %s";
    public static final String PLUGIN_SELF_DISABLED = "PluginManager will be disabled...";
    public static final String PLUGIN_DISABLE_SUCCESS = "Disabled Plugin %s.";
    public static final String PLUGIN_DISABLING = "Disabling Plugin %s...";
    public static final String PLUGIN_ENABLE_SUCCESS = "Enabled Plugin %s.";
    public static final String PLUGIN_ENABLING = "Enabling Plugin %s.";
    public static final String PLUGIN_LOAD_FAILURE = "Unable to load Plugin %s: %s";
    public static final String PLUGIN_LOAD_SUCCESS = "Loaded Plugin %s.";
    public static final String PLUGIN_LOADING = "Loading Plugin %s with File %s...";

    // Package private Message Strings:
    static final String CONFIG_LOAD_SUCCESS = "Successfully loaded Configuration.";
    static final String CONFIG_LOADING = "Loading Configuration...";
    static final String CONFIG_MISSING_PATH = "Missing path %s in 'config.yml'. Using default value.";
    static final String DESCRIPTION_FILE_LOAD_FAILURE = "Unable to load description File: %s";
    static final String FILE_LOAD = "Loaded File %s.";

    // Private Message Strings:
    private static final String DEFAULT_BUNDLE = "Using default Resource.";
    private static final String FOUND_CUSTOM_BUNDLE = "Found custom Resource: %s.";
    private static final String FOUND_BUNDLE = "Using Resource: %s.";
    private static final String KEY_NOT_FOUND_CUSTOM = "Missing translation key %s in Resource %s. Using default Resource.";
    private static final String KEY_NOT_FOUND = "Missing or wrong translation key %s.";
    private static final String LOCALE_NOT_SET = "No locale set. %s";
    private static final String LOCALE_SET = "Locale set to %s. %s";
    private static final String MESSAGES_LOADING = "Loading Message System...";

    // Private Message Strings for Player:
    private static final String NO_MESSAGES_LOADED = "\u00a74Error: \u00a7cNo messages loaded...";
    private static final String KEY_NOT_FOUND_PLAYER = "\u00a74Error: \u00a7cUnable to find message \u00a74%s\u00a7c.";

    // Class Variables:
    private static final String MESSAGES_FILE = "resources/messages";
    private static Messages instance;
    private ResourceBundle localBundle;
    private ResourceBundle customBundle;

    public Messages(@Nullable Locale locale) {
        PluginManager.logToConsole(Level.INFO, MESSAGES_LOADING);

        if (locale != null) {
            try {
                this.customBundle = ResourceBundle.getBundle(MESSAGES_FILE, locale,
                        new CustomBundleClassLoader(Messages.class.getClassLoader(), PluginManager.getInstance()));

                PluginManager.getInstance().getLogger().log(Level.INFO, String.format(LOCALE_SET, locale.toString(),
                        String.format(FOUND_CUSTOM_BUNDLE, this.customBundle.getBaseBundleName())));
            } catch (MissingResourceException ex) {
                this.customBundle = null;
            }

            try {
                this.localBundle = ResourceBundle.getBundle(MESSAGES_FILE, locale);

                if (this.customBundle == null) {
                    PluginManager.getInstance().getLogger().log(Level.INFO, String.format(LOCALE_SET, locale.toString(),
                            String.format(FOUND_BUNDLE, this.localBundle.getBaseBundleName())));
                }
            } catch (MissingResourceException ex) {
                this.localBundle = ResourceBundle.getBundle(MESSAGES_FILE);

                if (this.customBundle == null) {
                    PluginManager.getInstance().getLogger().log(Level.INFO, String.format(LOCALE_SET, locale.toString(),
                            DEFAULT_BUNDLE));
                }
            }
        } else {
            this.localBundle = ResourceBundle.getBundle(MESSAGES_FILE);

            PluginManager.getInstance().getLogger().log(Level.INFO, String.format(LOCALE_NOT_SET, DEFAULT_BUNDLE));
        }

        PluginManager$Command.setPermissionMessage(this.translate("global_noPermission"));
        PluginManager$Command.setUsageMessage(this.translate("global_commandUsage"));
    }

    public void setInstance(Messages newInstance) {
        instance = newInstance;
    }

    @NotNull
    private String translate(@NotNull String key, @Nullable Object... params) {
        if (customBundle != null) {
            try {
                if (params != null) {
                    return MessageFormat.format(customBundle.getString(key), params);
                } else {
                    return customBundle.getString(key);
                }
            } catch (MissingResourceException ex) {
                PluginManager.logToConsole(Level.INFO, String.format(KEY_NOT_FOUND_CUSTOM, ex.getKey(),
                        customBundle.getBaseBundleName()));
            }
        }

        try {
            if (params != null) {
                return MessageFormat.format(localBundle.getString(key), params);
            } else {
                return localBundle.getString(key);
            }
        } catch (MissingResourceException ex) {
            PluginManager.logToConsole(Level.SEVERE, String.format(KEY_NOT_FOUND, ex.getKey()));
            return KEY_NOT_FOUND_PLAYER;
        }

    }

    @NotNull
    public static String tl(boolean errPrefix, @NotNull String key, @Nullable Object... params) {
        if (instance != null) {
            if (params != null) {
                return errPrefix ? instance.translate("global_errorPrefix") + instance.translate(key, params) :
                        instance.translate(key, params);
            } else {
                return errPrefix ? instance.translate("global_errorPrefix") + instance.translate(key) :
                        instance.translate(key);
            }
        } else {
            return NO_MESSAGES_LOADED;
        }
    }

    private static class CustomBundleClassLoader extends ClassLoader {

        private File dataFolder;

        CustomBundleClassLoader(ClassLoader classLoader, PluginManager instance) {
            super(classLoader);
            this.dataFolder = instance.getDataFolder();
        }

        public URL getResource(String name) {
            File file = new File(this.dataFolder, name);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException var4) {
                    return null;
                }
            }

            return null;
        }

        public InputStream getResourceAsStream(String name) {
            File file = new File(this.dataFolder, name);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException var4) {
                    return null;
                }
            }

            return null;
        }
    }
}