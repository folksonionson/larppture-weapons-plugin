package org.bukkit.entity;
public interface Player extends HumanEntity, org.bukkit.command.CommandSender {
    org.bukkit.inventory.PlayerInventory getInventory();
    void playSound(org.bukkit.Location location, org.bukkit.Sound sound, float volume, float pitch);
    void setResourcePack(String url);
    void setResourcePack(String url, byte[] hash);
    void setResourcePack(String url, byte[] hash, String prompt);
    void setResourcePack(String url, byte[] hash, String prompt, boolean force);
    void setResourcePack(java.util.UUID uuid, String url, byte[] hash);
    void setResourcePack(java.util.UUID uuid, String url, byte[] hash, String prompt);
    void setResourcePack(java.util.UUID uuid, String url, byte[] hash, String prompt, boolean force);
}
