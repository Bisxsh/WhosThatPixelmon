package com.bisxsh.whosthatpixelmon;

import com.bisxsh.whosthatpixelmon.managers.TimeManager;
import com.google.inject.Inject;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.*;

@Mod(
        modid = Whosthatpixelmon.MOD_ID,
        name = Whosthatpixelmon.MOD_NAME,
        version = Whosthatpixelmon.VERSION
)

@Plugin(
        id = "whosthatpixelmon",
        name = "whosthatpixelmon",
        description = "A ChatGame plugin for pixelmon to mimic the 'Whos that Pixelmon' intervals from the show",
        version = "1.0.0",
        authors = "Bisxsh",
        dependencies = {@Dependency(id = "realmap"), @Dependency(id = "pixelmon")}
)

public class Whosthatpixelmon {

    public static final String MOD_ID = "whosthatpixelmon";
    public static final String MOD_NAME = "WhosThatPixelmon";
    public static final String VERSION = "1.0-SNAPSHOT";

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    private Logger logger;

    public Whosthatpixelmon() {
    }


    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException {
        logger.info("WhosThatPixelmon has started");
        setTimeInterval();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("WhosThatPixelmon has stopped");
    }

    public Logger getLogger() {
        return logger;
    }

    public void setTimeInterval() throws IOException {
        new TimeManager(this).setChatGameTimer();
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
}
