package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import com.bisxsh.whosthatpixelmon.mapItem.MapMaker;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
                            BroadcastManager.sendPlayerBroadcast(txt, player);
                        }
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return CommandResult.success();
                }).build();
        //

        //Command to obtain a specific map by fileName
        CommandSpec getMaps = CommandSpec.builder()
                .description(Text.of("Get a specific pixelmon's sprite maps"))
                .permission("whosthatpixelmon.map")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("spritePath")))
                )
                .executor((CommandSource src, CommandContext args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of("This is a player command"));
                        return CommandResult.success();
                    }
                    Player p = (Player) src;
                    Asset spriteAsset = WhosThatPixelmon.getInstance()
                            .getSpriteAsset(args.<String>getOne("spritePath").get());
                    try {
                        MapMaker mapMaker = new MapMaker(spriteAsset.getFileName());
                        p.getInventory().offer(mapMaker.getHiddenMap());
                        p.getInventory().offer(mapMaker.getRevealedMap());
                        return CommandResult.success();
                    } catch (IOException e) {
                        WhosThatPixelmon.getInstance().getLogger().info(
                                "[Whos That Pixelmon] Error getting the sprite files for "
                                        +args.<String>getOne("spritePath").get()
                        );
                        return CommandResult.success();
                    }
                })
                .build();
        //

        //Command to obtain all maps for a specific pixelmon
        CommandSpec getFileNames = CommandSpec.builder()
                .description(Text.of("Get all file names for a specific pokemon by dex number"))
                .permission("whosthatpixelmon.map")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("dexNum")))
                )
                .executor((CommandSource src, CommandContext args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of("This is a player command"));
                        return CommandResult.success();
                    }
                    Player p = (Player) src;

                    try {
                        Asset fileNamesAsset = WhosThatPixelmon.getInstance().getFileNamesAsset();
                        List<String> fileNames = fileNamesAsset.readLines();
                        String dexString = String.format("%03d", args.<Integer>getOne("dexNum").get());
                        ArrayList<String> filesFound = new ArrayList<>();

                        for (String name : fileNames) {
                            if (name.substring(0,3).equals(dexString)) {
                                filesFound.add(name);
                            }
                        }

                        BroadcastManager.sendPlayerBroadcast(Text.of("File names:"), p);
                        for (String file : filesFound) {
                            p.sendMessage(Text.of(TextColors.GREEN, TextStyles.BOLD, file));
                        }
                    } catch (IOException e) {
                        WhosThatPixelmon.getInstance().getLogger().info(
                                "[Whos That Pixelmon] Error accessing sprite files directory"
                        );
                    }
                    return CommandResult.success();
                }).build();
        //


        //Setup the /wtp parent command
        CommandSpec wtp = CommandSpec.builder()
                .description (Text.of("Start 'Whos that Pixelmon'"))
                .permission("whosthatpixelmon.command.start")
                .child(startChatGame, "start")
                .child(getMaps, "getmaps")
                .child(getFileNames, "getfilenames")
                .build();
        Sponge.getCommandManager().register(WhosThatPixelmon.getInstance(), wtp, "wtp",  "whosthatpixelmon");
        //

    }
}
