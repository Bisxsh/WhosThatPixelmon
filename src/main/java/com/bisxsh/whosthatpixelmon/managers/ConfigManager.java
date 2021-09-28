package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private ArrayList<String> itemRewards, commandsList;
    private ArrayList<Integer> itemAmounts;
    private int minTime, maxTime;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    public ConfigManager() throws IOException {
        initialSetup();
        itemRewards = new ArrayList<>();
        itemAmounts = new ArrayList<>();
    }

    private ConfigurationNode loadRootNode() throws IOException {
        File configFile = new File("config/whosthatpixelmon/whosthatpixelmon.conf");
        loader = HoconConfigurationLoader.builder()
                .setFile(configFile).build();
        ConfigurationNode rootNode = loader.load();
        return rootNode;
    }

    public Boolean loadRewards() throws IOException {
        ConfigurationNode rootNode = loadRootNode();

        try {
            Boolean rewardsEnabled = rootNode.getNode("itemsEnabled").getBoolean();
            if (!rewardsEnabled) {
                return false;
            }
        } catch (Exception e) {
            Whosthatpixelmon.getInstance().getLogger()
                    .warn("[Whos that Pixelmon] itemsEnabled was unable to be read, defaulting to true");
        }

        List<? extends ConfigurationNode> list = rootNode.getNode("item").getChildrenList();
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ConfigurationNode node = list.get(i);
            itemRewards.add(node.getNode("name").getString());
            itemAmounts.add(node.getNode("amount").getInt());
        }
        return true;
    }

    public Boolean loadCommands() throws IOException {
        ConfigurationNode rootNode = loadRootNode();
        Boolean commandsEnabled = rootNode.getNode("commandsEnabled").getBoolean();

        if (!commandsEnabled) {
           return false;
        }

        commandsList = new ArrayList<>();
        List<? extends ConfigurationNode> list = rootNode.getNode("commands").getChildrenList();
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ConfigurationNode node = list.get(i);
            commandsList.add(node.getString());
        }
        return true;
    }

    public Boolean loadRevealAnswer() throws IOException {
        ConfigurationNode rootNode = loadRootNode();
        return rootNode.getNode("revealAnswer").getBoolean();
    }

    public void loadTimeIntervals(Whosthatpixelmon mainClass) {
        try {
            ConfigurationNode node = loadRootNode().getNode("time");
            minTime = node.getNode("minimumTimeInterval").getInt();
            maxTime = node.getNode("maximumTimeInterval").getInt();
        } catch (Exception e) {
            mainClass.getLogger().warn("[Whos that Pixelmon] Unable to read time intervals from config, the default values will be used.");
            minTime = 30;
            maxTime = 35;
        }

    }

    public int loadGuessingTime() throws IOException {
        int guessingTime;
        ConfigurationNode rootnode = loadRootNode();
        try {
            guessingTime = rootnode.getNode("guessingTime").getInt();
        } catch (Exception e) {
            Whosthatpixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read guessingTIme from config, default value was used");
            guessingTime = 30;
        }
        return guessingTime;
    }

    private void initialSetup() throws IOException {
        if (!fileExists()) {
            Asset defaultConfigAsset = Whosthatpixelmon.getInstance().getDefaultConfigAsset();
            try {
                defaultConfigAsset.copyToDirectory(Whosthatpixelmon.getInstance().getConfigPath());
            } catch (IOException e) {
                Whosthatpixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to create default config file");
            }
        }
    }

    private Boolean fileExists() throws IOException {
        File configFile = new File("config/whosthatpixelmon/whosthatpixelmon.conf");
        if (configFile.exists()) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getItemRewards() {
        return itemRewards;
    }

    public ArrayList<Integer> getItemAmounts() {
        return itemAmounts;
    }

    public ArrayList<String> getCommandsList() {
        return commandsList;
    }

    public int getMinTime() {
        return minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }
}
