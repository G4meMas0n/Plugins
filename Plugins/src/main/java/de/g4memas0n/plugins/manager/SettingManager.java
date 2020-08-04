package de.g4memas0n.plugins.manager;

public class SettingManager {
    private boolean logToConsole;
    private boolean printStackTrace;

    // Object: boolean | Variable: logToConsole
    void setLogToConsole(boolean value) {
        this.logToConsole = value;
    }

    public boolean isLogToConsole() {
        return logToConsole;
    }

    // Object: boolean | Variable: printStackTrace
    void setPrintStackTrace(boolean value) {
        this.printStackTrace = value;
    }

    public boolean isPrintStackTrace() {
        return this.printStackTrace;
    }
}
