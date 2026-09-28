package org.bukkit.plugin;
public class EventException extends Exception {
    public EventException() { super(); }
    public EventException(String message) { super(message); }
    public EventException(Throwable cause) { super(cause); }
}
