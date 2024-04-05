package me.toddydev.bukkit.menus.product;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import me.toddydev.bukkit.menus.gateway.GatewayMenu;
import me.toddydev.core.model.order.gateway.Gateway;
import me.toddydev.core.model.product.Product;
import me.toddydev.core.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ProductMenu extends SimpleInventory {

    private Product product;
    public ProductMenu(Product product) {
        super(
                "product-menu",
                "Loja Virtual - " + product.getName(),
                9*3
        );

        this.product = product;
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        Player player = viewer.getPlayer();

        editor.setItem(12, InventoryItem.of(new ItemBuilder(Material.EXP_BOTTLE, 0).name("§aComprar")
                .lore(
                        "§eCusto: §aR$" +  String.format(new Locale("pt", "BR"), "%.2f",(product.getPrice() + (product.getPrice() * (0.99 / 100)))) + " §eou §6" + product.getCash() + " cash§e."
                ).build()).defaultCallback(callback -> {
                    callback.setCancelled(true);
                    player.closeInventory();

                    new GatewayMenu(product).init().openInventory(player);
                })
        );

        editor.setItem(14, InventoryItem.of(new ItemBuilder(Material.SKULL_ITEM, 3).name("§aPresenteie um jogador")
                .lore("§eEnvie um presente para um jogador.").texture("a03bd00421729cd635cd3b48243430ad47cf707018a5916ff59549d5ecd6f879").build())
                .defaultCallback(callback -> {
                    callback.setCancelled(true);
                }));
    }
}
