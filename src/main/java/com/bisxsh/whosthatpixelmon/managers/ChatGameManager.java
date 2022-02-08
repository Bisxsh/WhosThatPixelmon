package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.bisxsh.whosthatpixelmon.listeners.ChatListener;
import com.bisxsh.whosthatpixelmon.mapItem.MapMaker;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.util.ResourceLocation;
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

public class ChatGameManager {

    private Player winner;
    private ItemStack hiddenMap, revealedMap;
    private String pokemonName, pokemonForm;

    private Whosthatpixelmon mainClass;
    private ChatListener chatListener;
    private PlayerManager playerManager;
    private MapMaker mapMaker;


    public ChatGameManager() throws IOException, URISyntaxException {
        this.mainClass = Whosthatpixelmon.getInstance();
        mapMaker = new MapMaker();

        this.hiddenMap = mapMaker.getHiddenMap();
        this.revealedMap = mapMaker.getRevealedMap();
        this.pokemonName = mapMaker.getPokemonName();
        this.pokemonForm = mapMaker.getPokemonForm();

        playerManager = new PlayerManager(hiddenMap, revealedMap);

    }

    public void startChatGame() throws IOException {

        //Starting broadcast
        Text txt = Text.builder("'Whos that Pixelmon' will begin in 5 seconds." +
                        " Have an empty main hand to participate").build();
        BroadcastManager.getInstance().sendBroadcast(txt);
        //

        //Give participating players the hidden map
        ChatGameManager chatGameManager = this;
        Task.builder()
                .delay(5, TimeUnit.SECONDS)
                .execute(() -> {
                    playerManager.sendPlayersHiddenMap();
                    chatListener = new ChatListener(pokemonName, pokemonForm, chatGameManager);
                    Sponge.getEventManager().registerListeners(mainClass, chatListener);
                }).submit(mainClass);
        //

        int guessingTime = new ConfigManager().loadGuessingTime();
        Task.builder()
                .delay(guessingTime+5, TimeUnit.SECONDS)
                .execute(() -> {
                    if (winner == null) {
                        try {
                            if (new ConfigManager().loadRevealAnswer()) {
                                String answerString = new StringBuilder("It's ").append(getDisplayedAnswer()).toString();
                                Text answerText = Text.builder("Nobody guessed correctly in time. ")
                                        .append(Text.builder(answerString)
                                                .color(TextColors.RED).style(TextStyles.RESET).build())
                                        .build();
                                BroadcastManager.getInstance().sendBroadcast(answerText);
                                playerManager.sendPlayersRevealedMap();
                            } else {
                                defaultNoGuess();
                            }

                        } catch (IOException e) {
                            Whosthatpixelmon.getInstance().getLogger()
                                    .warn("[Whos that Pixelmon] revealAnswer config was not read correctly, defaulting to false");
                            defaultNoGuess();
                            e.printStackTrace();
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
        BroadcastManager.getInstance().sendBroadcast(Text.of("Nobody guessed correctly in time"));
        playerManager.sendPlayersRevealedMap();
    }

    public void processWinner(Player winner) throws IOException, InterruptedException {
        this.winner = winner;
        playerManager.sendPlayersRevealedMap();

        //Broadcast winner
        Text txt = Text.builder(winner.getName()+" guessed correctly. It's "+ getDisplayedAnswer()).build();
        BroadcastManager.getInstance().sendBroadcast(txt);
        //

        //Give player reward
        giveReward(winner);
        //

        endChatGame();
    }

    private void giveReward(Player winner) throws IOException {
        ConfigManager configManager = new ConfigManager();
        if (configManager.loadRewards()) {
            new RewardManager().giveReward(winner, configManager);
        }
        if (configManager.loadCommands()) {
            ArrayList<String> commandsList = configManager.getCommandsList();
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
//        mainClass.setTimeInterval();
        mapMaker.deleteSprite();
    }

}
