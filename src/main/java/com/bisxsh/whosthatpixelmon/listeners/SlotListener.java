package com.bisxsh.whosthatpixelmon.listeners;

import com.bisxsh.whosthatpixelmon.managers.PlayerManager;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Optional;

public class SlotListener {

    private Inventory storedSlot;
    private ItemStack mapItem;
    private Player player;
    private PlayerManager playerManager;

    public SlotListener (Inventory storedSlot, Player player, PlayerManager playerManager) {
        this.storedSlot = storedSlot;
        this.mapItem = storedSlot.peek().get();
        this.player = player;
        this.playerManager = playerManager;
    }

    //Prevents item being moved to off-hand
    @Listener
    public void onItemAffected(AffectSlotEvent event) {
        List<SlotTransaction> transactions = event.getTransactions();
        for (SlotTransaction transaction : transactions) {
            if (transaction.getSlot().equals(storedSlot)) {
                event.setCancelled(true);
            }
        }
    }
    //

    //Prevents item being moved in inventory
    @Listener
    public void onItemClicked(ClickInventoryEvent event) {
        Optional<Slot> slotClicked = event.getSlot();
        if (slotClicked.isPresent()) {
            Inventory slot = slotClicked.get();
            Optional<ItemStack> itemStored = slot.peek();
            if (itemStored.equals(storedSlot.peek())) {
                event.setCancelled(true);
            } else {
                if (!storedSlot.contains(mapItem)) {
                    event.getCursorTransaction().setValid(false);
                    storedSlot.set(mapItem);
                }
            }
        }
    }
    //

    //Prevents item from being dropped
    @Listener
    public void onItemDropped(DropItemEvent.Dispense event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            Player playerFromEvent = optionalPlayer.get();
            if (playerFromEvent.equals(player)) {
                event.setCancelled(true);
                MessageChannel playerChannel = MessageChannel.fixed(player);
                Text txt = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                        .append(Text.builder("You can not drop items while the chat game is active")
                                .color(TextColors.RED).style(TextStyles.RESET).build()).build();
                playerChannel.send(Text.of(txt));
            }
        }
    }
    //

    //Prevent item from being placed in an item frame
    @Listener
    public void onInteractEntity(InteractEntityEvent.Secondary.MainHand event) {
        Entity entity = event.getTargetEntity();
        if (entity.getType() == EntityTypes.ITEM_FRAME) {
            if (!entity.get(Keys.REPRESENTED_ITEM).isPresent()) {
                event.setCancelled(true);
            }
        }
    }
    //

    //Prevents user from logging off with items in their inventory
    @Listener
    public void onPlayerDiscconect(ClientConnectionEvent.Disconnect event) {
        Player playerFromEvent = event.getTargetEntity();
        if (playerFromEvent.equals(player)) {
            playerManager.removeDisconnectedMap(storedSlot, player);
        }
    }
    //

    public void setMapItem(ItemStack mapItem) {
        this.mapItem = mapItem;
    }
}
