package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import com.bisxsh.whosthatpixelmon.listeners.ChatListener;
import com.bisxsh.whosthatpixelmon.mapItem.MapMaker;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.bisxsh.whosthatpixelmon.managers.BroadcastManager.getText;

public class ChatGameManager {

    private Player winner;
    private final String pokemonName, pokemonForm;

    private final WhosThatPixelmon mainClass;
    private ChatListener chatListener;
    private final PlayerManager playerManager;
    private final MapMaker mapMaker;


    public ChatGameManager() throws IOException, URISyntaxException {
        this.mainClass = WhosThatPixelmon.getInstance();
        mapMaker = new MapMaker();

        ItemStack hiddenMap = mapMaker.getHiddenMap();
        ItemStack revealedMap = mapMaker.getRevealedMap();
        this.pokemonName = mapMaker.getPokemonName();
        this.pokemonForm = mapMaker.getPokemonForm();

        playerManager = new PlayerManager(hiddenMap, revealedMap);

    }

    public void startChatGame() throws IOException {
        //Starting broadcast
        Text txt = Text.builder(ConfigManager.getInstance().getStartingMessage()).build();
        BroadcastManager.sendBroadcast(txt);
        //

        //Give participating players the hidden map
        Task.builder()
                .delay(5, TimeUnit.SECONDS)
                .execute(() -> {
                    playerManager.sendPlayersHiddenMap();
                    chatListener = new ChatListener(pokemonName, pokemonForm, this);
                    Sponge.getEventManager().registerListeners(mainClass, chatListener);
                }).submit(mainClass);
        //

        int guessingTime = ConfigManager.getInstance().getGuessingTime();
        Task.builder()
                .delay(guessingTime+5, TimeUnit.SECONDS)
                .execute(() -> {
                    if (winner == null) {
                        if (ConfigManager.getInstance().shouldRevealAnswer()) {
                            String answerString = new StringBuilder(ConfigManager.getInstance().getRevealedAnswerMessage())
                                    .append(getDisplayedAnswer()).toString();
                            Text answerText = Text.builder(ConfigManager.getInstance().getNoAnswerMessage()+"! ")
                                    .append(Text.builder(answerString)
                                            .color(TextColors.RED).style(TextStyles.RESET).build())
                                    .build();
                            BroadcastManager.sendBroadcastUnformatted(answerText);
                            playerManager.sendPlayersRevealedMap();
                        } else {
                            defaultNoGuess();
                        }

                        try {
                            endChatGame();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).submit(mainClass);

    }

    public void defaultNoGuess() {
        BroadcastManager.sendBroadcast(getText(ConfigManager.getInstance().getNoAnswerMessage()));
        playerManager.sendPlayersRevealedMap();
    }

    public void processWinner(Player winner) throws IOException, InterruptedException {
        this.winner = winner;
        playerManager.sendPlayersRevealedMap();

        //Broadcast winner
        Text txt = Text.builder(winner.getName())
                .append(getText(ConfigManager.getInstance().getGuessedMessage()))
                .append(getText(ConfigManager.getInstance().getRevealedAnswerMessage()))
                .append(getText(getDisplayedAnswer()))
                .build();
        BroadcastManager.sendBroadcast(txt);
        //

        //Give player reward
        giveReward(winner);
        //

        endChatGame();
    }

    private void giveReward(Player winner) {
        if (ConfigManager.getInstance().areRewardsEnabled()) {
            new RewardManager().giveReward(winner);
        }
        if (ConfigManager.getInstance().areCommandsEnabled()) {
            ArrayList<String> commandsList = ConfigManager.getInstance().getCommandsList();
            for (String command : commandsList) {
                if (command.contains("<player>")) {
                    command = command.replace("<player>", winner.getName());
                }
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
            }
        }
    }

    private String getDisplayedAnswer() {
        String answer;
        if (pokemonForm != null) {
            answer = new StringBuilder(pokemonName)
                    .append(" (")
                    .append(pokemonForm)
                    .append(")")
                    .toString();
        } else {
            answer = pokemonName;
        }
        return answer;
    }

    public void endChatGame() throws IOException, InterruptedException {
        Sponge.getEventManager().unregisterListeners(chatListener);
        mainClass.setTimeInterval();
        mapMaker.deleteSprite();
    }
}
