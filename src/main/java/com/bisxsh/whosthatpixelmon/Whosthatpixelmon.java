package com.bisxsh.whosthatpixelmon;

import com.bisxsh.whosthatpixelmon.managers.BroadcastManager;
import com.bisxsh.whosthatpixelmon.managers.CommandManager;
import com.bisxsh.whosthatpixelmon.managers.ConfigManager;
import com.bisxsh.whosthatpixelmon.managers.TimeManager;
import com.bisxsh.whosthatpixelmon.mapItem.MapHandler;
import com.bisxsh.whosthatpixelmon.mapItem.MapMaker;
import com.google.inject.Inject;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Mod(
        modid = Whosthatpixelmon.MOD_ID,
        name = Whosthatpixelmon.MOD_NAME,
        version = Whosthatpixelmon.VERSION
)

@Plugin(
        id = "whosthatpixelmon",
        name = "whosthatpixelmon",
        description = "A ChatGame plugin for pixelmon to mimic the 'Whos that Pixelmon' intervals from the show",
        version = "1.2",
        authors = "Bisxsh",
        dependencies = {@Dependency(id = "realmap"), @Dependency(id = "pixelmon")}
)

public class Whosthatpixelmon {

    public static final String MOD_ID = "whosthatpixelmon";
    public static final String MOD_NAME = "WhosThatPixelmon";
    public static final String VERSION = "1.0-SNAPSHOT";
    private static Whosthatpixelmon INSTANCE = null;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    public Path getConfigPath() {
        return configPath;
    }

    @Inject
    private PluginContainer pluginContainer;
    private TimeManager timeManager;

    @Inject
    private Logger logger;

    public Whosthatpixelmon(){}

    @Listener
    public void onGameInit(GamePreInitializationEvent event) {
        INSTANCE = this;
    }

    public static Whosthatpixelmon getInstance() {
        return INSTANCE;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException {
        logger.info("WhosThatPixelmon has started");
        timeManager = new TimeManager();
        new BroadcastManager(new ConfigManager().loadPrefix());
        new CommandManager().setupCommands();
//        setTimeInterval();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("WhosThatPixelmon has stopped");
    }

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event) {
        //Check if player has map and remove to fix Players retaining map if server stopped during Chat-Game runtime
        Player player = event.getTargetEntity();
        Iterable<Inventory> playerInv = player.getInventory().slots();
        List<Text> lore = MapMaker.getLore();
        MapHandler mapHandler = new MapHandler();
        for (Inventory slot : playerInv) {
            if (slot.peek().isPresent()) {
                ItemStack item = slot.peek().get();
                Optional<List<Text>> itemLore = item.get(Keys.ITEM_LORE);
                if (itemLore.isPresent()) {
                    if (itemLore.get().equals(lore)) {
                        mapHandler.removeMap(slot, item, player);
                    }
                }
            }
        }
        //
    }

    public Logger getLogger() {
        return logger;
    }

    public void setTimeInterval() throws IOException {
        timeManager.setChatGameTimer();
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public Asset getFileNamesAsset() {
        Asset fileNamesAsset = Sponge.getAssetManager()
                .getAsset(getPluginContainer(), "spriteFileNames.txt").get();
        return fileNamesAsset;
    }

    public Asset getSpriteAsset(String path) {
        String spritePath = new StringBuilder("sprites/").append(path).toString();
        Asset pokemonSpriteAsset = Sponge.getAssetManager().getAsset(getPluginContainer(), spritePath).get();
        return pokemonSpriteAsset;
    }

    public Asset getDefaultConfigAsset() {
        Asset defaultConfigAsset = Sponge.getAssetManager()
                .getAsset(getPluginContainer(), "whosthatpixelmon.conf").get();
        return defaultConfigAsset;
    }
}
