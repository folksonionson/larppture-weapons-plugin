package org.bukkit.plugin;
public interface Plugin {
    String getName();
    java.util.logging.Logger getLogger();
    org.bukkit.Server getServer();
    org.bukkit.configuration.file.FileConfiguration getConfig();
    void saveDefaultConfig();
    void reloadConfig();
}
