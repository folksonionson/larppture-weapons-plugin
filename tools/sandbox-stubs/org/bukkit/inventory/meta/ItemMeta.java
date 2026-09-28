package org.bukkit.inventory.meta;
public interface ItemMeta extends Cloneable, org.bukkit.persistence.PersistentDataHolder {
    void setDisplayName(String name);
    String getDisplayName();
    boolean hasLore();
    void setLore(java.util.List<String> lore);
    void setUnbreakable(boolean unbreakable);
    boolean isUnbreakable();
    void addItemFlags(org.bukkit.inventory.ItemFlag... itemFlags);
    boolean addEnchant(org.bukkit.enchantments.Enchantment ench, int level, boolean ignoreLevelRestrictions);
    void setItemModel(org.bukkit.NamespacedKey model);
    org.bukkit.NamespacedKey getItemModel();
    void setCustomModelData(Integer data);
    int getCustomModelData();
    org.bukkit.inventory.meta.ItemMeta clone();
}
