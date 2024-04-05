package me.toddydev.bukkit.listeners.connection;

import me.toddydev.bukkit.BukkitMain;
import me.toddydev.core.cache.Caching;
import me.toddydev.core.database.tables.Tables;
import me.toddydev.core.player.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        User user = Tables.getUsers().find(event.getUniqueId());

        if (user == null) {
            user = new User(event.getUniqueId(), event.getName());
            Tables.getUsers().create(user);
        }

        Caching.getUserCache().add(user);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Caching.getNpcCache().findAll().forEach(npc -> npc.show(event.getPlayer()));
                } catch (Exception e) {}
            }
        }.runTaskLater(BukkitMain.getInstance(), 15L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Caching.getUserCache().remove(Caching.getUserCache().find(event.getPlayer().getUniqueId()));
    }
}
