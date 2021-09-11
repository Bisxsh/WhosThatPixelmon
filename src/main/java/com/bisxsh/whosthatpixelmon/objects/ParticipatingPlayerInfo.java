package com.bisxsh.whosthatpixelmon.objects;

import com.bisxsh.whosthatpixelmon.listeners.SlotListener;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;

public class ParticipatingPlayerInfo {

    private Player player;
    private Inventory storedSlot;
    private SlotListener slotListener;

    public ParticipatingPlayerInfo(Player player, Inventory storedSlot, SlotListener slotListener) {
        this.player = player;
        this.storedSlot = storedSlot;
        this.slotListener = slotListener;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getStoredSlot() {
        return storedSlot;
    }

    public SlotListener getSlotListener() {
        return slotListener;
    }
}
