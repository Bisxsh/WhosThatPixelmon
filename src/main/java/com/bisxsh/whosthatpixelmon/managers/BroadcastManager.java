package com.bisxsh.whosthatpixelmon.managers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

public class BroadcastManager {

    private static BroadcastManager INSTANCE = null;

    public static BroadcastManager getInstance() {
        return INSTANCE;
    }

    private Text prefix;

    public BroadcastManager (String prefix) {
        this.prefix = Text.builder(prefix).color(TextColors.YELLOW).style(TextStyles.BOLD).build();
        INSTANCE = this;
    }

    public void sendBroadcast (Text message) {
        Sponge.getServer().getBroadcastChannel().send(prefix.concat(getFormattedMessage(message)));
    }

    public void sendPlayerBroadcast (Text message, Player player) {
        player.getMessageChannel().send(prefix.concat(getFormattedMessage(message)));
    }

    private Text getFormattedMessage(Text message) {
        Text colouredMessage = Text.of(TextColors.GREEN, TextStyles.BOLD, message);
        Text spaced = Text.builder(" ").build().concat(colouredMessage);
        return spaced;
    }
}
