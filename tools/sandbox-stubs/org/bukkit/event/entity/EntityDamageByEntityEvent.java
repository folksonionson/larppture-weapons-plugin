package org.bukkit.event.entity;
public class EntityDamageByEntityEvent extends EntityEvent implements org.bukkit.event.Cancellable {
    public org.bukkit.entity.Entity getDamager() { throw new UnsupportedOperationException("stub"); }
    public double getDamage() { throw new UnsupportedOperationException("stub"); }
    public void setDamage(double damage) { throw new UnsupportedOperationException("stub"); }
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
