package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.*;


public class RewardManager {

    public RewardManager() {
    }

    public void giveReward(Player winner) {
        HashMap<String, Integer> rewards = ConfigManager.getInstance().getRewards();
        List<String> keysAsArray = new ArrayList<String>(rewards.keySet());
        String item = keysAsArray.get(new Random().nextInt(keysAsArray.size()));
        int amount = rewards.get(item);

        try {
            Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, item);
            ItemStackSnapshot itemStackSnapshot = optionalItemType.get().getTemplate();

            ItemStack itemStack = itemStackSnapshot.createStack();
            itemStack.setQuantity(amount);
            winner.getInventory().offer(itemStack);

            String rewardsString = new StringBuilder(ConfigManager.getInstance().getItemReceivedMessage()+" ")
                    .append(itemStackSnapshot.getType().getTranslation().get())
                    .append(" x").append(amount).append("!").toString();
            Text reward = Text.builder(rewardsString).build();
            BroadcastManager.sendPlayerBroadcast(reward, winner);
        } catch (Exception e) {
            new WhosThatPixelmon().getLogger().warn("[Whos That Pixelmon] Unable to create one of the reward ItemStacks");
        }
    }

    public String getItemName(ItemStack itemStack) {
        return itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation().get())).toString();
    }
}
