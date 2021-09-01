package com.bisxsh.whosthatpixelmon.listeners;

import com.bisxsh.whosthatpixelmon.managers.ChatGameManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ChatListener {

    private ArrayList<String> answers;
    private Player winner;
    private ChatGameManager chatGameManager;


    public ChatListener(String answer, String form, ChatGameManager chatGameManager) {
        this.chatGameManager = chatGameManager;
        answers = new ArrayList<>();
        answers.add(answer);

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
    public void onChatMessageRecieved(MessageChannelEvent.Chat event) throws IOException, InterruptedException {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            String messageSent = event.getRawMessage().toPlain();
            int answersSize = answers.size();
            for (int i = 0; i < answersSize; i++) {
                if (messageSent.equalsIgnoreCase(answers.get(i))) {
                    this.winner = optionalPlayer.get();
                    chatGameManager.processWinner(winner);
                }
            }
        }
    }
}
