package org.bukkit.persistence;
public interface PersistentDataContainer {
    <T, Z> void set(org.bukkit.NamespacedKey key, PersistentDataType<T, Z> type, Z value);
    <T, Z> Z get(org.bukkit.NamespacedKey key, PersistentDataType<T, Z> type);
    boolean has(org.bukkit.NamespacedKey key);
    void remove(org.bukkit.NamespacedKey key);
}
