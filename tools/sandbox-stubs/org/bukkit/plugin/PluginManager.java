package org.bukkit.plugin;
public interface PluginManager {
    void registerEvents(org.bukkit.event.Listener listener, Plugin plugin);
    void registerEvent(Class<? extends org.bukkit.event.Event> event, org.bukkit.event.Listener listener,
            org.bukkit.event.EventPriority priority, org.bukkit.plugin.EventExecutor executor, Plugin plugin);
    void registerEvent(Class<? extends org.bukkit.event.Event> event, org.bukkit.event.Listener listener,
            org.bukkit.event.EventPriority priority, org.bukkit.plugin.EventExecutor executor, Plugin plugin,
            boolean ignoreCancelled);
    void disablePlugin(Plugin plugin);
}
