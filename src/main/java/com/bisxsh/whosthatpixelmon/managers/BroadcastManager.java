package com.bisxsh.whosthatpixelmon.managers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class BroadcastManager {

    private static BroadcastManager INSTANCE = null;

    public static BroadcastManager getInstance() {
        return INSTANCE;
    }

    private Text prefix;

    public static Text toText(String msg) {
        return TextSerializers.FORMATTING_CODE.deserialize(msg);
    }

    public BroadcastManager (Text prefix) {
        this.prefix = prefix;
        INSTANCE = this;
    }

    public void sendBroadcast (Text message) {
        Sponge.getServer().getBroadcastChannel().send(prefix.concat(BroadcastManager.toText("&a&l"+message)));
    }

    public void sendPlayerBroadcast (Text message, Player player) {
        player.getMessageChannel().send(message);
    }
}
