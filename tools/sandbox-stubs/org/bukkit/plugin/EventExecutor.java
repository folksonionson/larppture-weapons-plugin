package org.bukkit.plugin;
public interface EventExecutor {
    void execute(org.bukkit.event.Listener listener, org.bukkit.event.Event event) throws EventException;
}
