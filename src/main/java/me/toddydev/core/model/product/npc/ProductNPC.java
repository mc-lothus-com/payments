package me.toddydev.core.model.product.npc;

import com.lothus.Lobby;
import com.lothus.services.Services;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.toddydev.bukkit.BukkitMain;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Location;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductNPC {

    private Skin skin;
    private Location location;
    private List<String> hologram;

    public NPC spawn() {
        NPC npc = Lobby.getNpcLib().createNPC(hologram);
        npc.setLocation(location);
        npc.setSkin(skin);
        npc.create();
        return npc;
    }
}
