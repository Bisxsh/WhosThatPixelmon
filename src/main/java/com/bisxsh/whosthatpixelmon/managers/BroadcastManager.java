package com.bisxsh.whosthatpixelmon.managers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class BroadcastManager {

    private static Text prefix = null;

    private static Text getPrefix() {
        if (prefix == null) {
            prefix = Text.builder(ConfigManager.getInstance().getPrefix())
                    .color(TextColors.YELLOW)
                    .style(TextStyles.BOLD)
                    .build();
        }
        return prefix;
    }

    public static Text getText(String message) {
        return Text.builder(message).build();
    }

    public static void sendBroadcastUnformatted (Text message) {
        Sponge.getServer().getBroadcastChannel().send(getPrefix().concat(message));
    }

    public static void sendBroadcast (Text message) {
        Sponge.getServer().getBroadcastChannel().send(getPrefix().concat(getFormattedMessage(message)));
    }

    public static void sendPlayerBroadcast (Text message, Player player) {
        player.getMessageChannel().send(getPrefix().concat(getFormattedMessage(message)));
    }

    private static Text getFormattedMessage(Text message) {
        Text colouredMessage = Text.of(TextColors.GREEN, TextStyles.BOLD, message);
        return Text.builder(" ").build().concat(colouredMessage);
    }
}
