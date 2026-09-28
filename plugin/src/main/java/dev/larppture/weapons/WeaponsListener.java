package dev.larppture.weapons;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownTrident;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Every legendary ability in one place.
 *
 * <ul>
 *   <li><b>Bloodbound Crusher (mace)</b> — bonus melee damage, lifesteal on kill.</li>
 *   <li><b>Reaper's Arc (scythe)</b> — bonus melee damage, right-click cone reap with wither.</li>
 *   <li><b>Stormcaller (golden trident)</b> — bonus melee damage, thrown hits call lightning.</li>
 *   <li><b>Wings of the Void (elytra)</b> — F (offhand-swap key) triggers a Super Dash.</li>
 *   <li><b>Chaos Cube</b> — right-click twists the cube: colour shift plus a random boon.</li>
 * </ul>
 */
public class WeaponsListener implements Listener {

    private final LarpptureWeapons plugin;
    private final Random random = new Random();

    public WeaponsListener(LarpptureWeapons plugin) {
        this.plugin = plugin;
    }

    /* ------------------------------------------------------------------ *
     * Resource pack push                                                  *
     * ------------------------------------------------------------------ */

    public void onJoin(PlayerJoinEvent event) {
        if (!this.plugin.getConfig().getBoolean("resource-pack.push-on-join", false)) {
            return;
        }
        String url = this.plugin.getConfig().getString("resource-pack.url", "");
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        Player player = event.getPlayer();
        String prompt = ChatColor.translateAlternateColorCodes('&',
                this.plugin.getConfig().getString("resource-pack.prompt",
                        "&4Larppture Weapons &7requires the &4Larppture &7resource pack."));
        boolean force = this.plugin.getConfig().getBoolean("resource-pack.force", false);
        String sha1 = this.plugin.getConfig().getString("resource-pack.sha1", "");
        if (sha1 != null && !sha1.trim().isEmpty()) {
            player.setResourcePack(player.getUniqueId(), url.trim(), hexToBytes(sha1.trim()), prompt, force);
        } else {
            player.setResourcePack(player.getUniqueId(), url.trim(), new byte[0], prompt, force);
        }
    }

    private byte[] hexToBytes(String hex) {
        String clean = hex.replaceAll("[^0-9a-fA-F]", "");
        if (clean.length() != 40) {
            return new byte[0];
        }
        byte[] out = new byte[20];
        for (int i = 0; i < 20; i++) {
            out[i] = (byte) Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16);
        }
        return out;
    }

    /* ------------------------------------------------------------------ *
     * Melee: bonus damage                                                 *
     * ------------------------------------------------------------------ */

    public void onMeleeHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player attacker = (Player) event.getDamager();
        LegendaryItem weapon = this.plugin.items().identify(attacker.getInventory().getItemInMainHand());
        if (weapon == null || !weapon.isEnabled(this.plugin)) {
            return;
        }
        double bonus = weapon.getConfiguredDamageBonus(this.plugin);
        if (bonus > 0D) {
            event.setDamage(event.getDamage() + bonus);
        }
        if (weapon == LegendaryItem.MACE) {
            attacker.getWorld().spawnParticle(Particle.CRIT, event.getEntity().getLocation().add(0D, 1D, 0D), 14, 0.4D, 0.4D, 0.4D, 0.2D);
        }
    }

    /* ------------------------------------------------------------------ *
     * Mace: lifesteal on kill                                             *
     * ------------------------------------------------------------------ */

    public void onKill(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        LegendaryItem weapon = this.plugin.items().identify(killer.getInventory().getItemInMainHand());
        if (weapon != LegendaryItem.MACE || !weapon.isEnabled(this.plugin)) {
            return;
        }
        double hearts = this.plugin.getConfig().getDouble("weapons.mace.lifesteal-hearts", 2.0D);
        if (hearts <= 0D) {
            return;
        }
        double healed = Math.min(killer.getMaxHealth(), killer.getHealth() + hearts * 2D);
        killer.setHealth(healed);
        killer.getWorld().spawnParticle(Particle.SCULK_SOUL, killer.getLocation().add(0D, 1D, 0D), 10, 0.3D, 0.5D, 0.3D, 0.05D);
        killer.playSound(killer.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8F, 0.7F);
        killer.sendMessage(ChatColor.DARK_RED + "The Bloodbound Crusher drinks " + ChatColor.RED
                + hearts + " hearts" + ChatColor.DARK_RED + " of " + victim.getName() + "'s soul.");
    }

    /* ------------------------------------------------------------------ *
     * Scythe: reap arc / Trident: (throw handled below) / Cube: twist     *
     * ------------------------------------------------------------------ */

    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        LegendaryItem item = this.plugin.items().identify(hand);
        if (item == null || !item.isEnabled(this.plugin)) {
            return;
        }
        switch (item) {
            case SCYTHE:
                reap(player);
                break;
            case RUBIK_CUBE:
                twist(player, hand);
                break;
            default:
                break;
        }
    }

    private void reap(Player player) {
        double cooldown = LegendaryItem.SCYTHE.getConfiguredCooldown(this.plugin);
        if (!this.plugin.cooldowns().tryUse(player.getUniqueId(), "scythe_reap", cooldown)) {
            tellCooldown(player, "The Reaper's Arc regathers...", this.plugin.cooldowns()
                    .remainingSeconds(player.getUniqueId(), "scythe_reap", cooldown));
            return;
        }
        double radius = this.plugin.getConfig().getDouble("weapons.scythe.arc-radius", 4.0D);
        double damage = this.plugin.getConfig().getDouble("weapons.scythe.arc-damage", 8.0D);
        int witherTicks = this.plugin.getConfig().getInt("weapons.scythe.wither-ticks", 40);

        Location origin = player.getEyeLocation();
        Vector facing = origin.getDirection().normalize();
        Collection<Entity> nearby = player.getWorld().getNearbyEntities(player.getLocation(), radius);
        int reaped = 0;
        for (Entity entity : nearby) {
            if (!(entity instanceof LivingEntity) || entity.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            Vector to = entity.getLocation().add(0D, 0.8D, 0D).subtract(origin).toVector();
            if (to.lengthSquared() < 1.0E-4D) {
                continue;
            }
            to.normalize();
            if (facing.dot(to) < 0.5D) { // ~60 degrees half-angle cone
                continue;
            }
            ((LivingEntity) entity).damage(damage, player);
            if (witherTicks > 0) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherTicks, 1));
            }
            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation().add(0D, 1D, 0D), 12, 0.4D, 0.5D, 0.4D, 0.05D);
            reaped++;
        }
        player.getWorld().spawnParticle(Particle.SCULK_SOUL, origin.add(facing.clone().multiply(1.5D)), 20, 0.6D, 0.3D, 0.6D, 0.06D);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5F, 1.6F);
        player.sendMessage(ChatColor.DARK_PURPLE + "You reap " + ChatColor.LIGHT_PURPLE + reaped
                + ChatColor.DARK_PURPLE + (reaped == 1 ? " soul." : " souls."));
    }

    private void twist(Player player, ItemStack cube) {
        double cooldown = LegendaryItem.RUBIK_CUBE.getConfiguredCooldown(this.plugin);
        if (!this.plugin.cooldowns().tryUse(player.getUniqueId(), "cube_twist", cooldown)) {
            tellCooldown(player, "The Chaos Cube resists...", this.plugin.cooldowns()
                    .remainingSeconds(player.getUniqueId(), "cube_twist", cooldown));
            return;
        }
        this.plugin.items().advanceCubeState(cube);
        int choice = this.random.nextInt(6);
        String boon;
        if (choice == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
            boon = ChatColor.AQUA + "Swiftness I (10s)";
        } else if (choice == 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 120, 0));
            boon = ChatColor.RED + "Strength I (6s)";
        } else if (choice == 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
            boon = ChatColor.LIGHT_PURPLE + "Regeneration I (5s)";
        } else if (choice == 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));
            boon = ChatColor.GOLD + "Fire Resistance (10s)";
        } else if (choice == 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0));
            boon = ChatColor.BLUE + "Night Vision (15s)";
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 0));
            boon = ChatColor.GREEN + "Leap I (10s)";
        }
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getEyeLocation(), 16, 0.4D, 0.3D, 0.4D, 0.08D);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.9F, 1.4F);
        player.sendMessage(ChatColor.GREEN + "The cube twists... " + boon);
    }

    /* ------------------------------------------------------------------ *
     * Trident: storm on thrown hit                                        *
     * ------------------------------------------------------------------ */

    public void onTridentLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownTrident)) {
            return;
        }
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) projectile.getShooter();
        LegendaryItem weapon = this.plugin.items().identify(shooter.getInventory().getItemInMainHand());
        if (weapon != LegendaryItem.GOLDEN_TRIDENT || !weapon.isEnabled(this.plugin)) {
            return;
        }
        PersistentDataContainer pdc = projectile.getPersistentDataContainer();
        pdc.set(new org.bukkit.NamespacedKey(this.plugin, "storm"), PersistentDataType.BYTE, (byte) 1);
    }

    public void onTridentHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ThrownTrident)) {
            return;
        }
        ThrownTrident trident = (ThrownTrident) event.getEntity();
        Byte storm = trident.getPersistentDataContainer()
                .get(new org.bukkit.NamespacedKey(this.plugin, "storm"), PersistentDataType.BYTE);
        if (storm == null) {
            return;
        }
        Entity hit = event.getHitEntity();
        if (hit == null) {
            return;
        }
        if (!(trident.getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) trident.getShooter();
        if (!this.plugin.cooldowns().tryUse(shooter.getUniqueId(), "trident_storm",
                LegendaryItem.GOLDEN_TRIDENT.getConfiguredCooldown(this.plugin))) {
            return;
        }
        double extra = this.plugin.getConfig().getDouble("weapons.golden_trident.lightning-damage", 4.0D);
        hit.getWorld().strikeLightning(hit.getLocation());
        if (hit instanceof LivingEntity && extra > 0D) {
            ((LivingEntity) hit).damage(extra, shooter);
        }
        shooter.playSound(shooter.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
        shooter.sendMessage(ChatColor.GOLD + "Stormcaller answers: the sky splits open!");
    }

    /* ------------------------------------------------------------------ *
     * Elytra: Super Dash on F (offhand swap key)                          *
     * ------------------------------------------------------------------ */

    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack chest = player.getInventory().getChestplate();
        LegendaryItem wings = this.plugin.items().identify(chest);
        if (wings != LegendaryItem.ELYTRA || !wings.isEnabled(this.plugin)) {
            return; // ordinary F swap keeps working
        }
        double cooldown = LegendaryItem.ELYTRA.getConfiguredCooldown(this.plugin);
        if (!this.plugin.cooldowns().tryUse(player.getUniqueId(), "void_dash", cooldown)) {
            event.setCancelled(true);
            tellCooldown(player, "The void needs a breath...", this.plugin.cooldowns()
                    .remainingSeconds(player.getUniqueId(), "void_dash", cooldown));
            return;
        }
        event.setCancelled(true); // F becomes the dash key while the wings are worn
        double power = this.plugin.getConfig().getDouble("weapons.elytra.dash-power", 2.2D);
        double lift = this.plugin.getConfig().getDouble("weapons.elytra.dash-lift", 0.35D);
        Vector dash = player.getLocation().getDirection().normalize().multiply(power);
        dash.setY(Math.max(dash.getY(), lift));
        player.setVelocity(dash);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0D, 1D, 0D), 24, 0.5D, 0.4D, 0.5D, 0.12D);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.9F, 1.3F);
        player.sendMessage(ChatColor.AQUA + "Super Dash! The void carries you.");
    }

    /* ------------------------------------------------------------------ */

    private void tellCooldown(Player player, String message, double secondsLeft) {
        player.sendMessage(ChatColor.GRAY + message + " (" + ChatColor.WHITE
                + String.format(java.util.Locale.ROOT, "%.1f", secondsLeft) + "s" + ChatColor.GRAY + ")");
    }

}
