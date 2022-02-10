package com.bisxsh.whosthatpixelmon.mapItem;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class MapHandler {

    private Inventory playerInv;

    public MapHandler() {
    }

    public Inventory showHiddenMap(Player player, ItemStack hiddenMap) {
        Inventory storedSlot = null;
        player.setItemInHand(HandTypes.MAIN_HAND, hiddenMap);


        playerInv = player.getInventory();
        Inventory filteredInv = playerInv.query(QueryOperationTypes.ITEM_TYPE.of(ItemTypes.FILLED_MAP));

        //Compare lore and get slot in player inv
        Optional<List<Text>> optMapLore = hiddenMap.get(Keys.ITEM_LORE);
        if (optMapLore.isPresent()){
            List<Text> mapLore = optMapLore.get();
            for(Inventory slot : filteredInv.slots()) {
                ItemStack stack = slot.peek().get();
                List<Text> lore = stack.get(Keys.ITEM_LORE).get();
                if (lore.equals(mapLore)) {
                    storedSlot = slot;
                }
            }
        }
        //
        return storedSlot;
    }

    public void showRevealedMap(Inventory storedSlot, ItemStack revealedMap) {
        storedSlot.set(revealedMap);
    }

    public void removeMap(Inventory storedSlot, ItemStack item, Player player) {
        if (storedSlot.contains(item)) {
            storedSlot.set(ItemStackSnapshot.NONE.createStack());
        } else {
            for (Inventory slot : player.getInventory().slots()) {
                if (slot.contains(item)) {
                    slot.set(ItemStackSnapshot.NONE.createStack());
                    if (storedSlot.peek().isPresent()) {
                        slot.set(storedSlot.peek().get());
                    }
                }
            }
        }
    }

}
