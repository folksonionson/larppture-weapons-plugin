package dev.larppture.weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /lwp — give, list, inspect, reload and pack-push for the legendary artifacts.
 */
public class WeaponsCommand implements CommandExecutor, TabCompleter {

    private final LarpptureWeapons plugin;

    public WeaponsCommand(LarpptureWeapons plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        String sub = args[0].toLowerCase(java.util.Locale.ROOT);
        if (sub.equals("give")) {
            return give(sender, args);
        }
        if (sub.equals("list")) {
            String line = ChatColor.DARK_RED + "Legendary artifacts: ";
            for (LegendaryItem item : LegendaryItem.values()) {
                line = line + ChatColor.GRAY + item.getId() + ChatColor.RESET + ", ";
            }
            sender.sendMessage(line.substring(0, line.length() - 2));
            return true;
        }
        if (sub.equals("reload")) {
            if (!sender.hasPermission("larppture.admin")) {
                sender.sendMessage(ChatColor.RED + "You lack the power (larppture.admin).");
                return true;
            }
            this.plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Larppture Weapons config reloaded.");
            return true;
        }
        if (sub.equals("pack")) {
            return pack(sender, args);
        }
        if (sub.equals("info")) {
            return info(sender);
        }
        sendHelp(sender);
        return true;
    }

    private boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("larppture.give")) {
            sender.sendMessage(ChatColor.RED + "You lack the power (larppture.give).");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /lwp give <player> <artifact> [amount]");
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No such player: " + args[1]);
            return true;
        }
        LegendaryItem item = LegendaryItem.byId(args[2]);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "Unknown artifact. Try /lwp list.");
            return true;
        }
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Math.max(1, Math.min(64, Integer.parseInt(args[3])));
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                return true;
            }
        }
        ItemStack stack = this.plugin.items().create(item, amount);
        target.getInventory().addItem(stack);
        target.sendMessage(ChatColor.GOLD + "You received " + item.getDisplayName() + ChatColor.GOLD + "!");
        if (target != sender) {
            sender.sendMessage(ChatColor.GREEN + "Gave " + item.getId() + " x" + amount + " to " + target.getName() + ".");
        }
        return true;
    }

    private boolean pack(CommandSender sender, String[] args) {
        if (!sender.hasPermission("larppture.admin")) {
            sender.sendMessage(ChatColor.RED + "You lack the power (larppture.admin).");
            return true;
        }
        String url = this.plugin.getConfig().getString("resource-pack.url", "");
        if (url == null || url.trim().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "resource-pack.url is not set in config.yml.");
            return true;
        }
        Player target;
        if (args.length >= 2) {
            target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "No such player: " + args[1]);
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "From console use: /lwp pack <player>");
            return true;
        }
        UUID packId = this.plugin.items().packUuid();
        String sha1 = this.plugin.getConfig().getString("resource-pack.sha1", "");
        byte[] hash = sha1 == null || sha1.trim().isEmpty() ? new byte[0] : hexToBytes(sha1.trim());
        target.setResourcePack(packId, url.trim(), hash,
                ChatColor.translateAlternateColorCodes('&',
                        this.plugin.getConfig().getString("resource-pack.prompt",
                                "&4Larppture Weapons &7requires the &4Larppture &7resource pack.")),
                this.plugin.getConfig().getBoolean("resource-pack.force", false));
        sender.sendMessage(ChatColor.GREEN + "Pack prompt sent to " + target.getName() + ".");
        return true;
    }

    private boolean info(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Players only.");
            return true;
        }
        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();
        LegendaryItem item = this.plugin.items().identify(hand);
        if (item == null) {
            sender.sendMessage(ChatColor.GRAY + "You hold no legend. (" + hand.getType().name() + ")");
            return true;
        }
        sender.sendMessage(item.getDisplayName() + ChatColor.GRAY + " [" + item.getId() + "] model=larppture:" + item.getModelId());
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "--- Larppture Weapons ---");
        sender.sendMessage(ChatColor.GRAY + "/lwp give <player> <artifact> [amount]");
        sender.sendMessage(ChatColor.GRAY + "/lwp list | /lwp info | /lwp reload | /lwp pack [player]");
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

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if (args.length == 1) {
            for (String sub : new String[]{"give", "list", "info", "reload", "pack"}) {
                if (sub.startsWith(args[0].toLowerCase(java.util.Locale.ROOT))) {
                    out.add(sub);
                }
            }
            return out;
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("pack"))) {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase(java.util.Locale.ROOT).startsWith(args[1].toLowerCase(java.util.Locale.ROOT))) {
                    out.add(player.getName());
                }
            }
            return out;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (LegendaryItem item : LegendaryItem.values()) {
                if (item.getId().startsWith(args[2].toLowerCase(java.util.Locale.ROOT))) {
                    out.add(item.getId());
                }
            }
        }
        return out;
    }
}
