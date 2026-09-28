package org.bukkit;
public interface Server {
    org.bukkit.plugin.PluginManager getPluginManager();
    org.bukkit.entity.Player getPlayer(String name);
    java.util.Collection<? extends org.bukkit.entity.Player> getOnlinePlayers();
}
