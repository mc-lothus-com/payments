package me.toddydev.bukkit.listeners.payment;

import me.toddydev.bukkit.BukkitMain;
import me.toddydev.bukkit.events.PaymentCompletedEvent;
import me.toddydev.bukkit.events.PaymentExpiredEvent;
import me.toddydev.core.Core;
import me.toddydev.core.api.actionbar.ActionBar;
import me.toddydev.core.api.taskchain.TaskChain;
import me.toddydev.core.cache.Caching;
import me.toddydev.core.model.product.Product;
import me.toddydev.core.model.product.actions.Action;
import me.toddydev.core.model.product.actions.type.ActionType;
import me.toddydev.core.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;
import static org.bukkit.Material.MAP;

public class PaymentListener implements Listener {

    @EventHandler
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Player player = event.getPlayer();
        User user = Caching.getUserCache().find(player.getUniqueId());
        Product product = Caching.getProductCache().findById(event.getOrder().getProductId());

        Action action = product.getActions().stream().filter(a -> a.getType().equals(ActionType.COLLECT)).findAny().orElse(null);

        player.playSound(player.getLocation(), action.getSound(), 5f, 5f);
        player.sendTitle(new Title(action.getScreen().getTitle().replace("&", "§"), action.getScreen().getSubtitle().replace("&", "§"), 10, 40, 10));

        player.sendMessage(action.getMessage()
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{product}", product.getName())
        );

        ActionBar.sendActionBar(player, action.getActionBar().replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{product}", product.getName())
        );

        TaskChain.newChain().add(new TaskChain.GenericTask() {
            @Override
            protected void run() {
                for (int slot : player.getInventory().all(Material.MAP).keySet()) {
                    ItemStack stack = player.getInventory().getItem(slot);
                    if (stack == null)continue;
                    if (stack.getType() != MAP)continue;

                    net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);

                    if (nms.getTag() == null)continue;
                    if (nms.getTag().getString("brpayments:order") == null)continue;

                    player.getInventory().setItem(slot, user.getItemInHand());
                    user.setItemInHand(null);
                    break;
                }

                product.getRewards().getCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                });

                product.getRewards().getItems().forEach(item -> {
                    player.getInventory().addItem(item.stack());
                });

                user.setTotalPaid(user.getTotalPaid() + product.getPrice());
            }
        }).execute();


    }

    @EventHandler
    public void onPaymentExpired(PaymentExpiredEvent event) {
        Player player = event.getPlayer();
        Product product = Caching.getProductCache().findById(event.getOrder().getProductId());

        Action action = product.getActions().stream().filter(a -> a.getType().equals(ActionType.EXPIRED)).findAny().orElse(null);

        player.playSound(player.getLocation(), action.getSound(), 5f, 5f);
        player.sendTitle(new Title(action.getScreen().getTitle().replace("&", "§"), action.getScreen().getSubtitle().replace("&", "§"), 10, 40, 10));

        player.sendMessage(action.getMessage()
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{product}", product.getName())
        );

        ActionBar.sendActionBar(player, action.getActionBar()
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{product}", product.getName())
        );
    }
}
