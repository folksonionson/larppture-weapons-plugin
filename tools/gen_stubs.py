#!/usr/bin/env python3
"""Generates the compile-time-only Bukkit API stub tree used by the offline
sandbox build (tools/build_plugin.mjs). The real Gradle build compiles against
the genuine io.papermc.paper:paper-api artifact; these stubs merely mirror the
exact signatures this plugin links against, so the WASM javac sandbox build
produces bytecode that links identically at runtime."""
import os

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "sandbox-stubs")

FILES = {}

def f(pkg, name, body):
    FILES[os.path.join(pkg.replace(".", "/"), name + ".java")] = (pkg, body)

# ---------------------------------------------------------------- org.bukkit
f("org.bukkit", "Material", """
public enum Material {
    AIR, MACE, NETHERITE_SWORD, TRIDENT, ELYTRA, NETHER_STAR;
}
""")

f("org.bukkit", "ChatColor", """
public enum ChatColor {
    BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY,
    DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, RESET, MAGIC, BOLD,
    STRIKETHROUGH, UNDERLINE, ITALIC;

    public static final char COLOR_CHAR = '\\u00a7';

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        throw new UnsupportedOperationException("stub");
    }
}
""")

f("org.bukkit", "NamespacedKey", """
public class NamespacedKey {
    public NamespacedKey(org.bukkit.plugin.Plugin plugin, String key) { throw new UnsupportedOperationException("stub"); }
    public NamespacedKey(String namespace, String key) { throw new UnsupportedOperationException("stub"); }
    public String toString() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit", "Particle", """
public enum Particle {
    CLOUD, CRIT, SOUL_FIRE_FLAME, ELECTRIC_SPARK, ENCHANTED_HIT, SCULK_SOUL, WHITE_ASH;
}
""")

f("org.bukkit", "Sound", """
public enum Sound {
    ENTITY_GENERIC_DRINK, ENTITY_WITHER_SPAWN, ITEM_TRIDENT_THUNDER,
    ENTITY_FIREWORK_ROCKET_LAUNCH, ENTITY_EXPERIENCE_ORB_PICKUP,
    ENTITY_LIGHTNING_BOLT_THUNDER, BLOCK_ANVIL_LAND;
}
""")

f("org.bukkit", "Location", """
public class Location implements Cloneable {
    public Location(World world, double x, double y, double z) { throw new UnsupportedOperationException("stub"); }
    public World getWorld() { throw new UnsupportedOperationException("stub"); }
    public double getX() { throw new UnsupportedOperationException("stub"); }
    public double getY() { throw new UnsupportedOperationException("stub"); }
    public double getZ() { throw new UnsupportedOperationException("stub"); }
    public Location add(double x, double y, double z) { throw new UnsupportedOperationException("stub"); }
    public Location add(org.bukkit.util.Vector vec) { throw new UnsupportedOperationException("stub"); }
    public Location subtract(Location other) { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.util.Vector toVector() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.util.Vector getDirection() { throw new UnsupportedOperationException("stub"); }
    public Location clone() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit", "World", """
public interface World {
    org.bukkit.entity.LightningStrike strikeLightning(Location location);
    java.util.Collection<org.bukkit.entity.Entity> getNearbyEntities(Location location, double radius);
    void spawnParticle(Particle particle, Location location, int count);
    void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ);
    void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);
    void playSound(Location location, Sound sound, float volume, float pitch);
}
""")

# ------------------------------------------------------- org.bukkit.persistence
f("org.bukkit.persistence", "PersistentDataHolder", """
public interface PersistentDataHolder {
    PersistentDataContainer getPersistentDataContainer();
}
""")

f("org.bukkit.persistence", "PersistentDataContainer", """
public interface PersistentDataContainer {
    <T, Z> void set(org.bukkit.NamespacedKey key, PersistentDataType<T, Z> type, Z value);
    <T, Z> Z get(org.bukkit.NamespacedKey key, PersistentDataType<T, Z> type);
    boolean has(org.bukkit.NamespacedKey key);
    void remove(org.bukkit.NamespacedKey key);
}
""")

f("org.bukkit.persistence", "NamespacedKey", """
package org.bukkit.persistence;
""")  # placeholder replaced below

FILES["org/bukkit/persistence/NamespacedKey.java"] = ("org.bukkit.persistence", "")

f("org.bukkit.persistence", "PersistentDataType", """
public interface PersistentDataType<T, Z> {
    PersistentDataType<Byte, Byte> BYTE = null;
    PersistentDataType<Integer, Integer> INTEGER = null;
    PersistentDataType<String, String> STRING = null;
    PersistentDataType<Long, Long> LONG = null;
}
""")

# ---------------------------------------------------------- org.bukkit.util
f("org.bukkit.util", "Vector", """
public class Vector implements Cloneable {
    public Vector() { throw new UnsupportedOperationException("stub"); }
    public Vector(double x, double y, double z) { throw new UnsupportedOperationException("stub"); }
    public double getX() { throw new UnsupportedOperationException("stub"); }
    public double getY() { throw new UnsupportedOperationException("stub"); }
    public double getZ() { throw new UnsupportedOperationException("stub"); }
    public Vector setY(double y) { throw new UnsupportedOperationException("stub"); }
    public Vector add(Vector other) { throw new UnsupportedOperationException("stub"); }
    public Vector multiply(double m) { throw new UnsupportedOperationException("stub"); }
    public double dot(Vector other) { throw new UnsupportedOperationException("stub"); }
    public double length() { throw new UnsupportedOperationException("stub"); }
    public double lengthSquared() { throw new UnsupportedOperationException("stub"); }
    public Vector normalize() { throw new UnsupportedOperationException("stub"); }
    public Vector clone() { throw new UnsupportedOperationException("stub"); }
}
""")

# ------------------------------------------------------- org.bukkit.entity
f("org.bukkit.entity", "Entity", """
public interface Entity extends org.bukkit.persistence.PersistentDataHolder {
    java.util.UUID getUniqueId();
    String getName();
    org.bukkit.Location getLocation();
    org.bukkit.World getWorld();
    void setVelocity(org.bukkit.util.Vector velocity);
    org.bukkit.util.Vector getVelocity();
    boolean isValid();
}
""")

f("org.bukkit.entity", "LivingEntity", """
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
""")

f("org.bukkit.entity", "HumanEntity", """
public interface HumanEntity extends LivingEntity, org.bukkit.inventory.PlayerInventoryHolder, org.bukkit.entity.ProjectileSource {
}
""")

f("org.bukkit.entity", "Player", """
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
""")

f("org.bukkit.entity", "ProjectileSource", """
public interface ProjectileSource {
}
""")

f("org.bukkit.entity", "Projectile", """
public interface Projectile extends Entity {
    ProjectileSource getShooter();
}
""")

f("org.bukkit.entity", "ThrownTrident", """
public interface ThrownTrident extends Projectile {
}
""")

f("org.bukkit.entity", "LightningStrike", """
public interface LightningStrike extends Entity {
}
""")

# ------------------------------------------------------ org.bukkit.inventory
f("org.bukkit.inventory", "ItemStack", """
public class ItemStack implements Cloneable {
    public ItemStack(org.bukkit.Material type) { throw new UnsupportedOperationException("stub"); }
    public ItemStack(org.bukkit.Material type, int amount) { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.Material getType() { throw new UnsupportedOperationException("stub"); }
    public int getAmount() { throw new UnsupportedOperationException("stub"); }
    public void setAmount(int amount) { throw new UnsupportedOperationException("stub"); }
    public boolean hasItemMeta() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.inventory.meta.ItemMeta getItemMeta() { throw new UnsupportedOperationException("stub"); }
    public boolean setItemMeta(org.bukkit.inventory.meta.ItemMeta itemMeta) { throw new UnsupportedOperationException("stub"); }
    public ItemStack clone() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.inventory", "ItemFlag", """
public enum ItemFlag {
    HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS,
    HIDE_PLACED_ON, HIDE_POTION_EFFECTS, HIDE_ADDITIONAL_TOOLTIP, HIDE_ARMOR_TRIM;
}
""")

f("org.bukkit.inventory", "EquipmentSlot", """
public enum EquipmentSlot {
    HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, BODY;
}
""")

f("org.bukkit.inventory", "PlayerInventoryHolder", """
public interface PlayerInventoryHolder {
    org.bukkit.inventory.PlayerInventory getInventory();
}
""")

f("org.bukkit.inventory", "Inventory", """
public interface Inventory {
    java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> addItem(org.bukkit.inventory.ItemStack... items);
}
""")

f("org.bukkit.inventory", "PlayerInventory", """
public interface PlayerInventory extends Inventory {
    org.bukkit.inventory.ItemStack getItemInMainHand();
    org.bukkit.inventory.ItemStack getItemInOffHand();
    org.bukkit.inventory.ItemStack getChestplate();
    void setChestplate(org.bukkit.inventory.ItemStack item);
}
""")

f("org.bukkit.inventory.meta", "ItemMeta", """
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
""")

# --------------------------------------------------------- org.bukkit.potion
f("org.bukkit.potion", "PotionEffectType", """
public class PotionEffectType {
    public static final PotionEffectType SPEED = null;
    public static final PotionEffectType STRENGTH = null;
    public static final PotionEffectType REGENERATION = null;
    public static final PotionEffectType FIRE_RESISTANCE = null;
    public static final PotionEffectType NIGHT_VISION = null;
    public static final PotionEffectType JUMP_BOOST = null;
    public static final PotionEffectType WITHER = null;
}
""")

f("org.bukkit.potion", "PotionEffect", """
public class PotionEffect {
    public PotionEffect(PotionEffectType type, int duration, int amplifier) { throw new UnsupportedOperationException("stub"); }
    public PotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) { throw new UnsupportedOperationException("stub"); }
}
""")

# --------------------------------------------------- org.bukkit.enchantments
f("org.bukkit.enchantments", "Enchantment", """
public abstract class Enchantment {
    public static final Enchantment UNBREAKING = null;
    public static final Enchantment MENDING = null;
    public static final Enchantment SHARPNESS = null;
}
""")

# --------------------------------------------------------- org.bukkit.plugin
f("org.bukkit.plugin", "Plugin", """
public interface Plugin {
    String getName();
    java.util.logging.Logger getLogger();
    org.bukkit.Server getServer();
    org.bukkit.configuration.file.FileConfiguration getConfig();
    void saveDefaultConfig();
    void reloadConfig();
}
""")

f("org.bukkit.plugin", "PluginManager", """
public interface PluginManager {
    void registerEvents(org.bukkit.event.Listener listener, Plugin plugin);
    void registerEvent(Class<? extends org.bukkit.event.Event> event, org.bukkit.event.Listener listener,
            org.bukkit.event.EventPriority priority, org.bukkit.plugin.EventExecutor executor, Plugin plugin);
    void registerEvent(Class<? extends org.bukkit.event.Event> event, org.bukkit.event.Listener listener,
            org.bukkit.event.EventPriority priority, org.bukkit.plugin.EventExecutor executor, Plugin plugin,
            boolean ignoreCancelled);
    void disablePlugin(Plugin plugin);
}
""")

f("org.bukkit.plugin.java", "JavaPlugin", """
public abstract class JavaPlugin implements org.bukkit.plugin.Plugin {
    public String getName() { throw new UnsupportedOperationException("stub"); }
    public java.util.logging.Logger getLogger() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.Server getServer() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.configuration.file.FileConfiguration getConfig() { throw new UnsupportedOperationException("stub"); }
    public void saveDefaultConfig() { throw new UnsupportedOperationException("stub"); }
    public void reloadConfig() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.command.PluginCommand getCommand(String name) { throw new UnsupportedOperationException("stub"); }
    protected void onEnable() { }
    protected void onDisable() { }
}
""")

# -------------------------------------------------------- org.bukkit.command
f("org.bukkit.command", "CommandSender", """
public interface CommandSender {
    void sendMessage(String message);
    boolean hasPermission(String permission);
    String getName();
}
""")

f("org.bukkit.command", "Command", """
public abstract class Command {
    public String getName() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.command", "CommandExecutor", """
public interface CommandExecutor {
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
""")

f("org.bukkit.command", "TabCompleter", """
public interface TabCompleter {
    java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
""")

f("org.bukkit.command", "PluginCommand", """
public final class PluginCommand extends Command {
    public void setExecutor(CommandExecutor executor) { throw new UnsupportedOperationException("stub"); }
    public void setTabCompleter(TabCompleter completer) { throw new UnsupportedOperationException("stub"); }
}
""")

# -------------------------------------------------- org.bukkit.configuration
f("org.bukkit.configuration.file", "FileConfiguration", """
public class FileConfiguration {
    public String getString(String path, String def) { throw new UnsupportedOperationException("stub"); }
    public double getDouble(String path, double def) { throw new UnsupportedOperationException("stub"); }
    public int getInt(String path, int def) { throw new UnsupportedOperationException("stub"); }
    public boolean getBoolean(String path, boolean def) { throw new UnsupportedOperationException("stub"); }
    public java.util.List<String> getStringList(String path) { throw new UnsupportedOperationException("stub"); }
}
""")

# ------------------------------------------------------------- org.bukkit
f("org.bukkit", "Server", """
public interface Server {
    org.bukkit.plugin.PluginManager getPluginManager();
    Player getPlayer(String name);
    java.util.Collection<? extends org.bukkit.entity.Player> getOnlinePlayers();
}
""")
# fix: Server must reference entity.Player
FILES["org/bukkit/Server.java"] = ("org.bukkit", """
public interface Server {
    org.bukkit.plugin.PluginManager getPluginManager();
    org.bukkit.entity.Player getPlayer(String name);
    java.util.Collection<? extends org.bukkit.entity.Player> getOnlinePlayers();
}
""")

# ------------------------------------------------------------ org.bukkit.event
f("org.bukkit.event", "Listener", """
public interface Listener {
}
""")

f("org.bukkit.event", "Event", """
public abstract class Event {
    public String getEventName() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.plugin", "EventException", """
public class EventException extends Exception {
    public EventException() { super(); }
    public EventException(String message) { super(message); }
    public EventException(Throwable cause) { super(cause); }
}
""")

f("org.bukkit.plugin", "EventExecutor", """
public interface EventExecutor {
    void execute(org.bukkit.event.Listener listener, org.bukkit.event.Event event) throws EventException;
}
""")

f("org.bukkit.event", "EventPriority", """
public enum EventPriority {
    LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR;
}
""")

f("org.bukkit.event", "Cancellable", """
public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancel);
}
""")

f("org.bukkit.event.block", "Action", """
public enum Action {
    LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_AIR, PHYSICAL;
}
""")

f("org.bukkit.event.player", "PlayerEvent", """
public abstract class PlayerEvent extends org.bukkit.event.Event {
    public org.bukkit.entity.Player getPlayer() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.player", "PlayerJoinEvent", """
public class PlayerJoinEvent extends PlayerEvent {
}
""")

f("org.bukkit.event.player", "PlayerInteractEvent", """
public class PlayerInteractEvent extends PlayerEvent implements org.bukkit.event.Cancellable {
    public org.bukkit.event.block.Action getAction() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.inventory.EquipmentSlot getHand() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.inventory.ItemStack getItem() { throw new UnsupportedOperationException("stub"); }
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.player", "PlayerSwapHandItemsEvent", """
public class PlayerSwapHandItemsEvent extends PlayerEvent implements org.bukkit.event.Cancellable {
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.entity", "EntityEvent", """
public abstract class EntityEvent extends org.bukkit.event.Event {
    public org.bukkit.entity.Entity getEntity() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.entity", "EntityDamageByEntityEvent", """
public class EntityDamageByEntityEvent extends EntityEvent implements org.bukkit.event.Cancellable {
    public org.bukkit.entity.Entity getDamager() { throw new UnsupportedOperationException("stub"); }
    public double getDamage() { throw new UnsupportedOperationException("stub"); }
    public void setDamage(double damage) { throw new UnsupportedOperationException("stub"); }
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.entity", "EntityDeathEvent", """
public class EntityDeathEvent extends EntityEvent {
    public org.bukkit.entity.LivingEntity getEntity() { throw new UnsupportedOperationException("stub"); }
    public java.util.List<org.bukkit.inventory.ItemStack> getDrops() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.entity", "ProjectileLaunchEvent", """
public class ProjectileLaunchEvent extends EntityEvent implements org.bukkit.event.Cancellable {
    public org.bukkit.entity.Projectile getEntity() { throw new UnsupportedOperationException("stub"); }
    public boolean isCancelled() { throw new UnsupportedOperationException("stub"); }
    public void setCancelled(boolean cancel) { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.event.entity", "ProjectileHitEvent", """
public class ProjectileHitEvent extends EntityEvent {
    public org.bukkit.entity.Projectile getEntity() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.entity.Entity getHitEntity() { throw new UnsupportedOperationException("stub"); }
    public org.bukkit.block.Block getHitBlock() { throw new UnsupportedOperationException("stub"); }
}
""")

f("org.bukkit.block", "Block", """
public interface Block {
    org.bukkit.Location getLocation();
    org.bukkit.Material getType();
}
""")


def main():
    # drop accidental placeholder
    FILES.pop("org/bukkit/persistence/NamespacedKey.java", None)
    for rel, (pkg, body) in FILES.items():
        path = os.path.join(ROOT, rel)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        header = "" if body.lstrip().startswith("package ") else ("package %s;\n" % pkg)
        with open(path, "w") as fh:
            fh.write(header + body.lstrip("\n"))
    print("wrote", len(FILES), "stub files")


if __name__ == "__main__":
    main()
