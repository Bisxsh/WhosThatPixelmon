package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.bisxsh.whosthatpixelmon.listeners.ChatListener;
import com.bisxsh.whosthatpixelmon.mapItem.MapMaker;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class ChatGameManager {

    private Player winner;
    private ItemStack hiddenMap, revealedMap;
    private String pokemonName, pokemonForm;

    private Whosthatpixelmon mainClass;
    private ChatListener chatListener;
    private PlayerManager playerManager;
    private MapMaker mapMaker;


    public ChatGameManager(Whosthatpixelmon mainClass) throws IOException, URISyntaxException {
        this.mainClass = mainClass;
        mapMaker = new MapMaker(mainClass);

        this.hiddenMap = mapMaker.getHiddenMap();
        this.revealedMap = mapMaker.getRevealedMap();
        this.pokemonName = mapMaker.getPokemonName();
        this.pokemonForm = mapMaker.getPokemonForm();

        playerManager = new PlayerManager(hiddenMap, revealedMap, mainClass);

    }

    public void startChatGame() {

        //Starting broadcast
        Text txt = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                .append(Text.builder("'Whos that Pixelmon' will begin in 5 seconds. Have an empty main hand to participate")
                        .color(TextColors.GREEN).style(TextStyles.RESET).build())
                .build();
        Sponge.getServer().getBroadcastChannel().send(txt);
        //

        //Give participating players the hidden map
        ChatGameManager chatGameManager = this;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                playerManager.sendPlayersHiddenMap();
                chatListener = new ChatListener(pokemonName, pokemonForm, chatGameManager);
                Sponge.getEventManager().registerListeners(mainClass, chatListener);
            }
        }, 5000);
        //


        //Check if answer has not been received after 30 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (winner == null) {
                    Text txt = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                            .append(Text.builder("Nobody guessed correctly in time")
                                    .color(TextColors.RED).style(TextStyles.BOLD).build())
                            .build();
                    Sponge.getServer().getBroadcastChannel().send(txt);
                    playerManager.sendPlayersRevealedMap();
                    try {
                        endChatGame();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 35000);
        //

    }

    public void processWinner(Player winner) throws IOException, InterruptedException {
        this.winner = winner;
        playerManager.sendPlayersRevealedMap();

        //Broadcast winner
        Text txt = Text.builder("[Chat Games] ").color(TextColors.YELLOW).style(TextStyles.BOLD)
                .append(Text.builder(winner.getName()+" guessed correctly. It's "+ getDisplayedAnswer())
                        .color(TextColors.GREEN).style(TextStyles.RESET).build())
                .build();
        Sponge.getServer().getBroadcastChannel().send(txt);
        //

        //Give player reward
        RewardManager rewardManager = new RewardManager();
        rewardManager.giveReward(winner);
        //

        endChatGame();
    }

    private String getDisplayedAnswer() {
        String answer;
        if (pokemonForm != null) {
            answer = new StringBuilder(pokemonName).append(" ("+pokemonForm+")").toString();
        } else {
            answer = pokemonName;
        }
        return answer;
    }

    public void endChatGame() throws IOException, InterruptedException {
        playerManager.removeListeners();
        mainClass.setTimeInterval();
        mapMaker.deleteSprite();
    }

}
