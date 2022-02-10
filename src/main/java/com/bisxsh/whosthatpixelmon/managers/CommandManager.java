package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class CommandManager {

    public void setupCommands() {
        // /Command to start the chat game
        CommandSpec startChatGame = CommandSpec.builder()
                .description(Text.of("Starts 'Whos that Pixelmon'"))
                .permission("whosthatpixelmon.comamnd.start")
                .executor((CommandSource src, CommandContext args) -> {
                    try {
                        new ChatGameManager().startChatGame();
                        if (src instanceof Player) {
                            Player player = (Player) src;
                            Text txt = Text.builder("Forcibly started 'Whos that Pixelmon'").build();
                            BroadcastManager.getInstance().sendPlayerBroadcast(txt, player);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return CommandResult.success();
                }).build();
        //

        CommandSpec getPokemonSprite = CommandSpec.builder()
                .description(Text.of("Gets Pokemon Sprite"))
                .permission("whosthatpixelmon.comamnd.start")
                .executor((CommandSource src, CommandContext args) -> {
                    if (src instanceof Player){
                        Player player = (Player) src;
                        PartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
                        Pokemon tradedPokemon = party.getFirstAblePokemon();
                        ResourceLocation spriteLocation = GuiResources.getPokemonSprite(tradedPokemon);
                    }
                    return CommandResult.success();
                }).build();

        //Setup the /wtp parent command
        CommandSpec wtp = CommandSpec.builder()
                .description (Text.of("Start 'Whos that Pixelmon'"))
                .permission("whosthatpixelmon.command.start")
                .child(startChatGame, "start")
                .build();
        Sponge.getCommandManager().register(Whosthatpixelmon.getInstance(), wtp, "wtp",  "whosthatpixelmon");
        //
    }
}
