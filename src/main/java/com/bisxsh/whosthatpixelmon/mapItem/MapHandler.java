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

public class MapHandler {

    public MapHandler() {
    }

    public Inventory showHiddenMap(Player player, ItemStack hiddenMap) {
        Inventory storedSlot = null;
        player.setItemInHand(HandTypes.MAIN_HAND, hiddenMap);

        Inventory playerInv = player.getInventory();
        Inventory filteredInv = playerInv.query(QueryOperationTypes.ITEM_TYPE.of(ItemTypes.FILLED_MAP));

        //Compare lore and get slot in player inv
        List<Text> mapLore = hiddenMap.get(Keys.ITEM_LORE).get();
        for(Inventory slot : filteredInv.slots()) {
            ItemStack stack = slot.peek().get();
            List<Text> lore = stack.get(Keys.ITEM_LORE).get();
            if (lore.equals(mapLore)) {
                storedSlot = slot;
            }
        }
        //
        return storedSlot;
    }

    public void showRevealedMap(Inventory storedSlot, ItemStack revealedMap) {
        storedSlot.set(revealedMap);
    }

    public void removeMap(Inventory storedSlot) {
            storedSlot.set(ItemStackSnapshot.NONE.createStack());
    }

}
