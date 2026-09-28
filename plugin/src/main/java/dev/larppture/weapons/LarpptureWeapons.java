package dev.larppture.weapons;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Larppture Weapons — legendary artifacts for Paper 1.21.11.
 *
 * <p>The plugin mints five legendary items (see {@link LegendaryItem}), gives
 * them their abilities ({@link WeaponsListener}), hands them out with
 * {@code /lwp} ({@link WeaponsCommand}) and pushes the companion resource
 * pack to joining players when enabled in {@code config.yml}.</p>
 */
public class LarpptureWeapons extends JavaPlugin {

    public static final String PDC_ID = "id";
    public static final String PDC_CUBE_STATE = "cube_state";

    private ItemFactory itemFactory;
    private Cooldowns cooldowns;

    public void onEnable() {
        saveDefaultConfig();
        this.itemFactory = new ItemFactory(this);
        this.cooldowns = new Cooldowns();

        registerListeners();

        WeaponsCommand command = new WeaponsCommand(this);
        org.bukkit.command.PluginCommand lwp = getCommand("lwp");
        if (lwp != null) {
            lwp.setExecutor(command);
            lwp.setTabCompleter(command);
        } else {
            getLogger().severe("Command 'lwp' is missing from plugin.yml - disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Larppture Weapons enabled: " + LegendaryItem.values().length + " legendary artifacts.");
        if (getConfig().getBoolean("resource-pack.push-on-join", false)) {
            getLogger().info("Resource pack push is ON: " + getConfig().getString("resource-pack.url", ""));
        }
    }

    /**
     * Explicit event registration (no annotation scanning): each ability is
     * bound with its own priority / ignoreCancelled semantics.
     */
    private void registerListeners() {
        final WeaponsListener listener = new WeaponsListener(this);
        org.bukkit.plugin.PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(org.bukkit.event.player.PlayerJoinEvent.class, listener,
                org.bukkit.event.EventPriority.MONITOR, (l, e) ->
                        ((WeaponsListener) l).onJoin((org.bukkit.event.player.PlayerJoinEvent) e), this);
        pm.registerEvent(org.bukkit.event.entity.EntityDamageByEntityEvent.class, listener,
                org.bukkit.event.EventPriority.HIGH, (l, e) ->
                        ((WeaponsListener) l).onMeleeHit((org.bukkit.event.entity.EntityDamageByEntityEvent) e),
                this, true);
        pm.registerEvent(org.bukkit.event.entity.EntityDeathEvent.class, listener,
                org.bukkit.event.EventPriority.MONITOR, (l, e) ->
                        ((WeaponsListener) l).onKill((org.bukkit.event.entity.EntityDeathEvent) e), this, true);
        pm.registerEvent(org.bukkit.event.player.PlayerInteractEvent.class, listener,
                org.bukkit.event.EventPriority.HIGH, (l, e) ->
                        ((WeaponsListener) l).onInteract((org.bukkit.event.player.PlayerInteractEvent) e), this);
        pm.registerEvent(org.bukkit.event.entity.ProjectileLaunchEvent.class, listener,
                org.bukkit.event.EventPriority.MONITOR, (l, e) ->
                        ((WeaponsListener) l).onTridentLaunch((org.bukkit.event.entity.ProjectileLaunchEvent) e),
                this, true);
        pm.registerEvent(org.bukkit.event.entity.ProjectileHitEvent.class, listener,
                org.bukkit.event.EventPriority.HIGH, (l, e) ->
                        ((WeaponsListener) l).onTridentHit((org.bukkit.event.entity.ProjectileHitEvent) e),
                this, true);
        pm.registerEvent(org.bukkit.event.player.PlayerSwapHandItemsEvent.class, listener,
                org.bukkit.event.EventPriority.HIGH, (l, e) ->
                        ((WeaponsListener) l).onSwapHands((org.bukkit.event.player.PlayerSwapHandItemsEvent) e), this);
    }

    public void onDisable() {
        getLogger().info("Larppture Weapons disabled. The legends sleep...");
    }

    public ItemFactory items() {
        return this.itemFactory;
    }

    public Cooldowns cooldowns() {
        return this.cooldowns;
    }
}
