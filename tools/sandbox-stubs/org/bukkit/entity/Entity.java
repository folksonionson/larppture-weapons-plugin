package org.bukkit.entity;
public interface Entity extends org.bukkit.persistence.PersistentDataHolder {
    java.util.UUID getUniqueId();
    String getName();
    org.bukkit.Location getLocation();
    org.bukkit.World getWorld();
    void setVelocity(org.bukkit.util.Vector velocity);
    org.bukkit.util.Vector getVelocity();
    boolean isValid();
}
