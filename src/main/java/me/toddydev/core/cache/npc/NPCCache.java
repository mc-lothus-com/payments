package me.toddydev.core.cache.npc;

import net.jitse.npclib.api.NPC;

import java.util.ArrayList;
import java.util.List;

public class NPCCache {

    private List<NPC> npcs = new ArrayList<>();

    public void add(NPC npc) {
        npcs.add(npc);
    }

    public void remove(NPC npc) {
        npcs.remove(npc);
    }

    public List<NPC> findAll() {
        return npcs;
    }
}
