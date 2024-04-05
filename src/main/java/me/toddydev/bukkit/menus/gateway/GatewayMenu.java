package me.toddydev.bukkit.menus.gateway;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import me.toddydev.bukkit.BukkitMain;
import me.toddydev.bukkit.events.PaymentCompletedEvent;
import me.toddydev.core.api.qrcore.ImageCreator;
import me.toddydev.core.cache.Caching;
import me.toddydev.core.database.tables.Tables;
import me.toddydev.core.model.order.Order;
import me.toddydev.core.model.order.gateway.Gateway;
import me.toddydev.core.model.order.gateway.type.GatewayType;
import me.toddydev.core.model.order.status.OrderStatus;
import me.toddydev.core.model.product.Product;
import me.toddydev.core.services.Services;
import me.toddydev.core.utils.item.ItemBuilder;
import me.toddydev.core.utils.keys.RandomKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public class GatewayMenu extends SimpleInventory {

    private Product product;

    public GatewayMenu(Product product) {
        super(
                "gateway-menu",
                "Loja Virtual - Método de Pagamento",
                9*3
        );

        this.product = product;
    }

    @Override
    protected void configureInventory(Viewer v, InventoryEditor e) {
        Player player = v.getPlayer();

        LothPlayer lp = Core.getPlayerController().get(player.getUniqueId());

        e.setItem(12, InventoryItem.of(new ItemBuilder(Material.DIAMOND, 0)
                        .name("§aAdquira via §2PIX§a!")
                        .lore(
                                "§eCusto: §2R$ §a" + String.format(new Locale("pt", "BR"), "%.2f",(product.getPrice() + (product.getPrice() * (0.99 / 100))))
                        ).build())
                .defaultCallback(event -> {
                    event.setCancelled(true);
                    player.closeInventory();

                    if (Caching.getOrdersCache().findByPayer(player.getUniqueId()) != null) {
                        player.sendMessage(BukkitMain.getMessagesConfig().getString("already-have-order").replace("&", "§"));
                        return;
                    }

                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        if (player.getInventory().getItem(i) != null) {
                            if (!product.getRewards().getItems().isEmpty()) {
                                player.sendMessage(BukkitMain.getMessagesConfig().getString("must-inventory-clean").replace("&", "§"));
                                return;
                            }
                        }
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Order order = Order.builder().payerId(player.getUniqueId()).referenceId(RandomKey.generateCode()).cost(product.getPrice() + (product.getPrice() * (0.99 / 100)))
                                    .status(OrderStatus.WAITING).gateway(Caching.getGatewaysCache().find(GatewayType.MERCADO_PAGO)).build();

                            order.setProductId(product.getId());

                            order = Services.getMercadoPagoService().create(order);

                            Caching.getOrdersCache().add(order);
                            ImageCreator.generateMap(order.getCode(), player);

                            Tables.getOrders().create(order);
                        }
                    }).start();
                })
        );

        e.setItem(14, InventoryItem.of(new ItemBuilder(Material.GOLD_INGOT, 0).name("§aAdquira com §6CASH§e!")
                .lore(
                        "§eCusto: §6" + product.getCash() + " cash."
                ).build())
                .defaultCallback(callback -> {
                    callback.setCancelled(true);
                    player.closeInventory();

                    if (lp.getCash() < product.getCash()) {
                        player.sendMessage("§cVocê não possui cash o suficiente para adquirir isso.");
                        return;
                    }

                    lp.setCash(lp.getCash() - product.getCash());

                    Order order = Order.builder().payerId(player.getUniqueId())
                                    .referenceId(RandomKey.generateCode()).gateway(new Gateway("", GatewayType.CASH))
                                    .status(OrderStatus.PAID).cost(product.getCash()).paymentId(RandomKey.generateCode()).productId(product.getId()).build();

                    Tables.getOrders().create(order);
                    Core.getDataPlayer().update(lp);

                    Bukkit.getPluginManager().callEvent(new PaymentCompletedEvent(player, order));
                })
        );
    }
}
