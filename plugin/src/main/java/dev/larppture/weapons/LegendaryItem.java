package dev.larppture.weapons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

/**
 * The five legendary artifacts of Larppture.
 *
 * <p>Each entry binds a vanilla base material (which defines the item's raw
 * behaviour: a mace smashes, a trident throws, an elytra equips...) to a custom
 * item model from the Larppture Weapons resource pack ({@code larppture:<id>})
 * and to the ability implemented in {@link WeaponsListener}.</p>
 */
public enum LegendaryItem {

    MACE("mace", Material.MACE,
            "&4Bloodbound Crusher",
            Arrays.asList(
                    "&8A war-mace quenched in dragon blood.",
                    "&8Every skull it cracks &4steals breath &8for its master.",
                    "",
                    "&7Bonus damage: &c+6.0",
                    "&7Lifesteal on kill: &c2 hearts",
                    "&8Smash attacks keep their vanilla brutality."),
            6.0D, 0.0D),

    SCYTHE("scythe", Material.NETHERITE_SWORD,
            "&5Reaper's Arc",
            Arrays.asList(
                    "&8The harvest blade of the hollow king.",
                    "&8Right-click to &5reap &8every soul in a wide arc.",
                    "",
                    "&7Bonus damage: &c+3.0",
                    "&7Reap: &d8.0 dmg &7in 4 blocks, wither 2s",
                    "&7Reap cooldown: &e5s"),
            3.0D, 5.0D),

    GOLDEN_TRIDENT("golden_trident", Material.TRIDENT,
            "&6Stormcaller",
            Arrays.asList(
                    "&8Forged from a fallen bolt of the sky god.",
                    "&8Thrown true, it &6calls the storm &8upon its prey.",
                    "",
                    "&7Bonus damage: &c+2.0",
                    "&7Thrown hit: &6lightning &7+ &c4.0 dmg",
                    "&7Storm cooldown: &e3s"),
            2.0D, 3.0D),

    ELYTRA("elytra", Material.ELYTRA,
            "&bWings of the Void",
            Arrays.asList(
                    "&8Feathers torn from the night itself.",
                    "&8While worn, press &bF &8to &bSuper Dash &8forward.",
                    "",
                    "&7Super Dash: &bburst of void speed",
                    "&7Dash cooldown: &e4s"),
            0.0D, 4.0D),

    RUBIK_CUBE("rubik_cube", Material.NETHER_STAR,
            "&aChaos Cube",
            Arrays.asList(
                    "&8Six faces, seven moods, infinite mischief.",
                    "&8Right-click to &atwist &8it and claim a random boon.",
                    "",
                    "&7Twist: random buff + colour shift",
                    "&7Twist cooldown: &e10s"),
            0.0D, 10.0D);

    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final double defaultDamageBonus;
    private final double defaultCooldown;

    LegendaryItem(String id, Material material, String displayName, List<String> lore,
                  double defaultDamageBonus, double defaultCooldown) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.defaultDamageBonus = defaultDamageBonus;
        this.defaultCooldown = defaultCooldown;
    }

    public String getId() {
        return this.id;
    }

    /** Model id inside the resource pack: {@code larppture:<id>}. */
    public String getModelId() {
        return this.id;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<String> getLore() {
        return new ArrayList<String>(this.lore);
    }

    public double getDefaultDamageBonus() {
        return this.defaultDamageBonus;
    }

    public double getDefaultCooldown() {
        return this.defaultCooldown;
    }

    public double getConfiguredDamageBonus(LarpptureWeapons plugin) {
        return plugin.getConfig().getDouble("weapons." + this.id + ".damage-bonus", this.defaultDamageBonus);
    }

    public double getConfiguredCooldown(LarpptureWeapons plugin) {
        return plugin.getConfig().getDouble("weapons." + this.id + ".cooldown", this.defaultCooldown);
    }

    public boolean isEnabled(LarpptureWeapons plugin) {
        return plugin.getConfig().getBoolean("weapons." + this.id + ".enabled", true);
    }

    public static LegendaryItem byId(String id) {
        for (LegendaryItem item : values()) {
            if (item.id.equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }
}
