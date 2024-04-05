package me.toddydev.bukkit;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.lothus.Lobby;
import com.squareup.okhttp.Cache;
import lombok.Getter;
import me.toddydev.bukkit.loaders.categories.CategoryLoader;
import me.toddydev.bukkit.loaders.commands.BukkitCommandLoader;
import me.toddydev.bukkit.loaders.gateways.GatewayLoader;
import me.toddydev.bukkit.loaders.listeners.ListenerLoader;
import me.toddydev.bukkit.loaders.products.ProductLoader;
import me.toddydev.bukkit.task.PayTask;
import me.toddydev.core.Core;
import me.toddydev.core.cache.Caching;
import me.toddydev.core.database.credentials.DatabaseCredentials;
import me.toddydev.core.database.tables.Tables;
import me.toddydev.core.model.product.Product;
import me.toddydev.core.plugin.BukkitPlugin;
import me.toddydev.core.utils.metrics.Metrics;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class BukkitMain extends BukkitPlugin {

    @Getter
    private static YamlConfiguration messagesConfig;

    @Getter
    private static NPCLib npcLib = Lobby.getNpcLib();
    @Override
    public void load() {
        saveDefaultConfig();
        loadConfig();

        Core.getDatabase().start(
                new DatabaseCredentials(
                        getConfig().getString("database.host"),
                        getConfig().getString("database.port"),
                        getConfig().getString("database.username"),
                        getConfig().getString("database.password"),
                        getConfig().getString("database.database")
                )
        );
    }

    @Override
    public void enable() {
        Tables.getUsers().create();
        Tables.getOrders().create();

        GatewayLoader.load(this);
        CategoryLoader.load(this);
        ProductLoader.load(this);

        ListenerLoader.load(this);

        InventoryManager.enable(this);

        BukkitCommandLoader.load(this);

        new Metrics(this, 19978);
        new PayTask().runTaskTimerAsynchronously(this, 0, 20*60);

        Bukkit.getScheduler().runTaskLater(this, new BukkitRunnable() {
            @Override
            public void run() {
                for (Product product : Caching.getProductCache().findAll()) {
                    NPC npc = product.getNpc().spawn();
                    Caching.getNpcCache().add(npc);
                }
            }
        }, 20L);
    }

    private void loadConfig() {
        File f = new File(getDataFolder(), "messages.yml");

        if (!f.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(f);
    }

    @Override
    public void disable() {
        Core.getDatabase().stop();
    }
}
