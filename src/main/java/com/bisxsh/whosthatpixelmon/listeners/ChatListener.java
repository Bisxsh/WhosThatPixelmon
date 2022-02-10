package com.bisxsh.whosthatpixelmon.listeners;

import com.bisxsh.whosthatpixelmon.managers.ChatGameManager;
import com.google.common.collect.Lists;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ChatListener {

    private final ArrayList<String> answers;
    private final ChatGameManager chatGameManager;


    public ChatListener(String answer, String form, ChatGameManager chatGameManager) {
        this.chatGameManager = chatGameManager;
        answers = Lists.newArrayList(answer);

        //Add form in answer as optional, usage is unlikely as they generally share the same outline
        if (form != null) {
            StringBuilder stringBuilder = new StringBuilder(form).append(" ").append(answer); // form_pixelmon
            answers.add(stringBuilder.toString());
            stringBuilder = new StringBuilder(answer).append(" ").append(form); //pixelmon_form
            answers.add(stringBuilder.toString());
            stringBuilder = new StringBuilder(answer).append(" (").append(form).append(")"); //pixelmon_(form)
            answers.add(stringBuilder.toString());
        }
        //

    }


    @Listener
    public void onChatMessageReceived(MessageChannelEvent.Chat event) throws IOException, InterruptedException {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            String messageSent = event.getRawMessage().toPlain();
            for (String answer : answers) {
                if (messageSent.equalsIgnoreCase(answer)) {
                    Player winner = optionalPlayer.get();
                    chatGameManager.processWinner(winner);
                }
            }
        }
    }
}
