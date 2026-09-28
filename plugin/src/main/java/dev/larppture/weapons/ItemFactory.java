package dev.larppture.weapons;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Mints and recognises legendary items.
 *
 * <p>Identity is stored in the item's {@link PersistentDataContainer}
 * ({@code larppture:id = <artifact id>}) so it survives renames, repairs and
 * server restarts. The look comes from the item-model component pointing at
 * {@code larppture:<id>} in the companion resource pack.</p>
 */
public class ItemFactory {

    private final LarpptureWeapons plugin;

    public ItemFactory(LarpptureWeapons plugin) {
        this.plugin = plugin;
    }

    private NamespacedKey idKey() {
        return new NamespacedKey(this.plugin, LarpptureWeapons.PDC_ID);
    }

    private NamespacedKey cubeStateKey() {
        return new NamespacedKey(this.plugin, LarpptureWeapons.PDC_CUBE_STATE);
    }

    /** Builds a fresh legendary item stack. */
    public ItemStack create(LegendaryItem item, int amount) {
        ItemStack stack = new ItemStack(item.getMaterial(), amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        meta.setDisplayName(color(item.getDisplayName()));
        meta.setLore(colorList(item.getLore()));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        // Subtle legendary glint without showing enchantments.
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        // Visuals: item model from the Larppture Weapons resource pack.
        meta.setItemModel(new NamespacedKey(this.plugin, item.getModelId()));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(idKey(), PersistentDataType.STRING, item.getId());
        if (item == LegendaryItem.RUBIK_CUBE) {
            pdc.set(cubeStateKey(), PersistentDataType.INTEGER, 1);
            meta.setCustomModelData(1);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    /** @return the legendary identity of a stack, or {@code null} for ordinary items. */
    public LegendaryItem identify(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }
        String id = meta.getPersistentDataContainer().get(idKey(), PersistentDataType.STRING);
        if (id == null) {
            return null;
        }
        LegendaryItem item = LegendaryItem.byId(id);
        if (item == null) {
            return null;
        }
        // Guard against items copied onto a different base material.
        return item.getMaterial() == stack.getType() ? item : null;
    }

    /** Reads the chaos-cube colour state (1..7). */
    public int cubeState(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return 1;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return 1;
        }
        Integer state = meta.getPersistentDataContainer().get(cubeStateKey(), PersistentDataType.INTEGER);
        return state == null ? 1 : state.intValue();
    }

    /** Advances the chaos-cube colour state and mirrors it into custom model data. */
    public void advanceCubeState(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        int state = cubeState(stack) % 7 + 1;
        meta.getPersistentDataContainer().set(cubeStateKey(), PersistentDataType.INTEGER, state);
        meta.setCustomModelData(state);
        stack.setItemMeta(meta);
    }

    public UUID packUuid() {
        String raw = this.plugin.getConfig().getString("resource-pack.uuid", "");
        if (raw == null || raw.trim().isEmpty()) {
            return UUID.fromString("1a7974e5-b2c4-4d61-9f3b-8c2d7e154a0b");
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ex) {
            String hex = Integer.toHexString(raw.trim().hashCode());
            while (hex.length() < 8) {
                hex = "0" + hex;
            }
            return UUID.fromString(hex + "-0000-4000-8000-000000000000");
        }
    }

    private String color(String input) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', input);
    }

    private java.util.List<String> colorList(java.util.List<String> input) {
        java.util.List<String> out = new java.util.ArrayList<String>(input.size());
        for (String line : input) {
            out.add(color(line));
        }
        return out;
    }
}
