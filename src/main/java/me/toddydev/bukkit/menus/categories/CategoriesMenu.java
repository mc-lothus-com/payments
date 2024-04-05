package me.toddydev.bukkit.menus.categories;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import me.toddydev.bukkit.menus.category.CategoryMenu;
import me.toddydev.core.cache.Caching;

import java.util.LinkedList;
import java.util.List;

public final class CategoriesMenu extends PagedInventory {
    public CategoriesMenu() {
        super(
                "category-menu",
                "Loja Virtual - Categorias",
                9*6
        );

    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        ViewerConfigurationImpl.Paged config = viewer.getConfiguration();

        config.itemPageLimit(7);
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer pagedViewer) {
        List<InventoryItemSupplier> items = new LinkedList<>();

        Caching.getCategoryCache().getCategories().forEach(category -> {
            items.add(() -> InventoryItem.of(category.stack()).defaultCallback(event -> {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                new CategoryMenu(category).init().openInventory(event.getPlayer());
            }));
        });

        return items;
    }
}
