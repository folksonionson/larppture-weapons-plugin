package org.bukkit.inventory;
public interface PlayerInventory extends Inventory {
    org.bukkit.inventory.ItemStack getItemInMainHand();
    org.bukkit.inventory.ItemStack getItemInOffHand();
    org.bukkit.inventory.ItemStack getChestplate();
    void setChestplate(org.bukkit.inventory.ItemStack item);
}
