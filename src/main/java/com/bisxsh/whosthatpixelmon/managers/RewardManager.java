package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;


public class RewardManager {

    private ArrayList<String> itemRewards;
    private ArrayList<Integer> itemAmounts;

    public RewardManager() {
    }

    public void giveReward(Player winner, ConfigManager configManager) throws IOException {
        int index = pickRandomReward(configManager);
        String item = itemRewards.get(index);
        int amount = itemAmounts.get(index);

        try {
            Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, item);
            ItemStackSnapshot itemStackSnapshot = optionalItemType.get().getTemplate();

            ItemStack itemStack = itemStackSnapshot.createStack();
            itemStack.setQuantity(amount);
            winner.getInventory().offer(itemStack);

            String rewardsString = new StringBuilder("You have received ")
                    .append(itemStackSnapshot.getType().getTranslation().get())
                    .append(" x").append(itemAmounts.get(index)).append("!").toString();
            Text reward = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                    .append(Text.builder(rewardsString)
                            .color(TextColors.GREEN).style(TextStyles.RESET).build())
                    .build();
            MessageChannel.fixed(winner).send(reward);
        } catch (Exception e) {
            new Whosthatpixelmon().getLogger().warn("[WhosThatPixelmon] Unable to create one of the reward ItemStacks");
        }
    }

    private int pickRandomReward(ConfigManager configManager) throws IOException {
        itemRewards = configManager.getItemRewards();
        itemAmounts = configManager.getItemAmounts();

        Random random = new Random();
        int index = random.nextInt(itemRewards.size());
        return index;
    }

    public String getItemName(ItemStack itemStack) {
        return itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation().get())).toString();
    }
}
