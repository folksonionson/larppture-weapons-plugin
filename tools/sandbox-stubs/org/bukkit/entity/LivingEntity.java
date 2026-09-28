package org.bukkit.entity;
public interface LivingEntity extends Entity {
    void damage(double amount);
    void damage(double amount, Entity source);
    double getHealth();
    void setHealth(double health);
    double getMaxHealth();
    boolean addPotionEffect(org.bukkit.potion.PotionEffect effect);
    org.bukkit.Location getEyeLocation();
    Player getKiller();
}
