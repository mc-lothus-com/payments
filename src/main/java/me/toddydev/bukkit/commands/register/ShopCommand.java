package me.toddydev.bukkit.commands.register;

import me.toddydev.bukkit.BukkitMain;
import me.toddydev.bukkit.commands.BukkitCommand;
import me.toddydev.bukkit.menus.IndexMenu;
import me.toddydev.bukkit.menus.categories.CategoriesMenu;
import me.toddydev.bukkit.menus.category.CategoryMenu;
import me.toddydev.core.cache.Caching;
import me.toddydev.core.model.product.categories.Category;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends BukkitCommand {
    public ShopCommand() {
        super(
                "shop"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (!(sender instanceof Player)) ? null : (Player) sender;

        if (player == null) {
            sender.sendMessage("§cEste comando só pode ser executado in-game.");
            return false;
        }

        if (args.length == 0) {
            IndexMenu menu = new IndexMenu().init();
            menu.openInventory(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("category")) {
            if (args.length == 1) {
                player.sendMessage(BukkitMain.getMessagesConfig().getString("command-shop-category-usage").replace("&", "§"));
                return false;
            }

            Category category = Caching.getCategoryCache().findIgnoreCase(args[1]);
            if (category == null) {
                player.sendMessage(BukkitMain.getMessagesConfig().getString("command-shop-category-not-found").replace("&", "§"));
                return false;
            }

            CategoryMenu menu = new CategoryMenu(category).init();
            menu.openInventory(player);
            return true;
        } else if (args[0].equalsIgnoreCase("categories")) {
            CategoriesMenu menu = new CategoriesMenu().init();
            menu.openInventory(player);
            return true;
        } else {
            player.sendMessage(BukkitMain.getMessagesConfig().getString("command-shop-usage").replace("&", "§"));
        }

        return false;
    }
}
