package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.bisxsh.whosthatpixelmon.listeners.SlotListener;
import com.bisxsh.whosthatpixelmon.mapItem.MapHandler;
import com.bisxsh.whosthatpixelmon.objects.ParticipatingPlayerInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private ItemStack hiddenMap, revealedMap;
    private MapHandler showMap;
    private ArrayList<ParticipatingPlayerInfo> participatingPlayerList;
    private Whosthatpixelmon mainClass;

    public PlayerManager (ItemStack hiddenMap, ItemStack revealedMap) {
        this.hiddenMap = hiddenMap;
        this.revealedMap = revealedMap;
        participatingPlayerList = new ArrayList<>();
        showMap = new MapHandler();
        this.mainClass = Whosthatpixelmon.getInstance();

    }

    public void sendPlayersHiddenMap() {
        Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();

        for (Player player : onlinePlayers) {
            if (player.getItemInHand(HandTypes.MAIN_HAND).get().isEmpty()) {

                Inventory storedSlot = showMap.showHiddenMap(player, hiddenMap);

                SlotListener slotListener = new SlotListener(storedSlot, player, this);
                Sponge.getEventManager().registerListeners(mainClass, slotListener);

                ParticipatingPlayerInfo participatingPlayerInfo = new ParticipatingPlayerInfo(player, storedSlot, slotListener);
                participatingPlayerList.add(participatingPlayerInfo);
            } else {
                Text errorTxt = Text.builder("Failed to give map as your main hand was not empty.")
                        .build();
                BroadcastManager.getInstance().sendPlayerBroadcast(errorTxt, player);
            }
        }
    }

    public void sendPlayersRevealedMap() {
        int listSize = participatingPlayerList.size();
        for (int i = 0; i < listSize; i++) {

            ParticipatingPlayerInfo playerInfo = participatingPlayerList.get(i);
            Inventory storedSlot = playerInfo.getStoredSlot();
            SlotListener listener = playerInfo.getSlotListener();

            Sponge.getEventManager().unregisterListeners(listener);
            showMap.removeMap(storedSlot, hiddenMap, playerInfo.getPlayer());

            showMap.showRevealedMap(storedSlot, revealedMap);
            Sponge.getEventManager().registerListeners(mainClass, listener);

            Task.builder()
                    .delay(5, TimeUnit.SECONDS)
                    .execute(() -> {
                        Sponge.getEventManager().unregisterListeners(listener);
                        showMap.removeMap(storedSlot, revealedMap, playerInfo.getPlayer());
                        removeListeners();
                    }).submit(Whosthatpixelmon.getInstance());
        }
    }

    public void removeDisconnectedMap(Inventory storedSlot, Player player) {
        showMap.removeMap(storedSlot, hiddenMap, player);
        showMap.removeMap(storedSlot, revealedMap, player);
    }

    public void removeListeners() {
        int listSize = participatingPlayerList.size();
        for (int i = 0; i < listSize; i++) {
            ParticipatingPlayerInfo playerInfo = participatingPlayerList.get(i);
            SlotListener listener = playerInfo.getSlotListener();
            Sponge.getEventManager().unregisterListeners(listener);
        }
    }
}
