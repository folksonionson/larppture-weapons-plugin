package org.bukkit.event.player;
public class PlayerInteractEvent extends PlayerEvent implements org.bukkit.event.Cancellable {
    public org.bukkit.event.block.Action getAction() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.inventory.EquipmentSlot getHand() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.inventory.ItemStack getItem() { throw new UnsupportedOperationException("stub"); }
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
