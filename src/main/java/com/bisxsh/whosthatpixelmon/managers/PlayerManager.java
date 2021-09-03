package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.bisxsh.whosthatpixelmon.listeners.SlotListener;
import com.bisxsh.whosthatpixelmon.mapItem.MapHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerManager {
    private ItemStack hiddenMap, revealedMap;
    private ArrayList<Player> disabledPlayers;
    private MapHandler showMap;
    private ArrayList<Object[]> participatingPlayerInfo;
    private Whosthatpixelmon mainClass;

    public PlayerManager (ItemStack hiddenMap, ItemStack revealedMap, Whosthatpixelmon mainClass) {
        this.hiddenMap = hiddenMap;
        this.revealedMap = revealedMap;
        disabledPlayers = new ArrayList<>();
        participatingPlayerInfo = new ArrayList<>();
        showMap = new MapHandler();
        this.mainClass = mainClass;

    }

    public void sendPlayersHiddenMap() {
        Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();

        for (Player player : onlinePlayers) {
            if (!disabledPlayers.contains(player)) { //Finds all online players with the chat-game enabled
                if (player.getItemInHand(HandTypes.MAIN_HAND).get().isEmpty()) {

                    Object[] playerInfo = new Object[3];
                    playerInfo[0] = player;

                    Inventory storedSlot = showMap.showHiddenMap(player, hiddenMap);
                    playerInfo[1] = storedSlot;

                    SlotListener slotListener = new SlotListener(storedSlot, player, this);
                    Sponge.getEventManager().registerListeners(mainClass, slotListener);
                    playerInfo[2] = slotListener;

                    participatingPlayerInfo.add(playerInfo);
                } else {
                    Text errorTxt = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                            .append(Text.builder("Failed to give map as your main hand was not empty.")
                                    .color(TextColors.RED).style(TextStyles.RESET).build())
                            .build();
                    MessageChannel.fixed(player).send(errorTxt);
                }
            }
        }
    }

    public void sendPlayersRevealedMap() {
        int listSize = participatingPlayerInfo.size();
        for (int i = 0; i < listSize; i++) {

            Object[] playerInfo = participatingPlayerInfo.get(i);
            Inventory storedSlot = (Inventory) playerInfo[1];
            SlotListener listener = (SlotListener) playerInfo[2];

            Sponge.getEventManager().unregisterListeners(listener);
            showMap.removeMap(storedSlot);

            showMap.showRevealedMap(storedSlot, revealedMap);
            Sponge.getEventManager().registerListeners(mainClass, listener);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Sponge.getEventManager().unregisterListeners(listener);
                    showMap.removeMap(storedSlot);
                }
            }, 5000);
        }
    }

    public void removeDisconnectedMap(Inventory storedSlot) {
        showMap.removeMap(storedSlot);
    }

    public void removeListeners() {
        int listSize = participatingPlayerInfo.size();
        for (int i = 0; i < listSize; i++) {
            Object[] playerInfo = participatingPlayerInfo.get(i);
            SlotListener listener = (SlotListener) playerInfo[2];
            Sponge.getEventManager().unregisterListeners(listener);
        }
    }
}
