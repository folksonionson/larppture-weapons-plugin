package org.bukkit.plugin.java;
public abstract class JavaPlugin implements org.bukkit.plugin.Plugin {
    public String getName() { throw new UnsupportedOperationException("stub"); }
    public java.util.logging.Logger getLogger() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.Server getServer() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.configuration.file.FileConfiguration getConfig() { throw new UnsupportedOperationException("stub"); }
    public void saveDefaultConfig() { throw new UnsupportedOperationException("stub"); }
    public void reloadConfig() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.command.PluginCommand getCommand(String name) { throw new UnsupportedOperationException("stub"); }
    protected void onEnable() { }
    protected void onDisable() { }
}
