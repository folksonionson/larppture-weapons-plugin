package org.bukkit;
public interface World {
    org.bukkit.entity.LightningStrike strikeLightning(Location location);
    java.util.Collection<org.bukkit.entity.Entity> getNearbyEntities(Location location, double radius);
    void spawnParticle(Particle particle, Location location, int count);
    void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ);
    void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);
    void playSound(Location location, Sound sound, float volume, float pitch);
}
