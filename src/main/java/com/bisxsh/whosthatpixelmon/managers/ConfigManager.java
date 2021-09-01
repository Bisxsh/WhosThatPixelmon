package com.bisxsh.whosthatpixelmon.managers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private ArrayList<String> itemRewards;
    private ArrayList<Integer> itemAmounts;
    private int minTime, maxTime;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    public ConfigManager() throws IOException {
        initialSetup();
        itemRewards = new ArrayList<>();
        itemAmounts = new ArrayList<>();
    }

    private ConfigurationNode loadRootNode() throws IOException {
        File configFile = new File("config/whosthatpixelmon.conf");
        loader = HoconConfigurationLoader.builder()
                .setFile(configFile).build();
        ConfigurationNode rootNode = loader.load();
        return rootNode;
    }

    public void loadRewards() throws IOException {

        ConfigurationNode rootNode = loadRootNode();
        List<? extends ConfigurationNode> list = rootNode.getNode("item").getChildrenList();
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ConfigurationNode node = list.get(i);
            itemRewards.add(node.getNode("name").getString());
            itemAmounts.add(node.getNode("amount").getInt());
        }
    }

    public void loadTimeIntervals() throws IOException {
        ConfigurationNode node = loadRootNode().getNode("time");
        minTime = node.getNode("minimumTimeInterval").getInt();
        maxTime = node.getNode("maximumTimeInterval").getInt();
    }

    private void initialSetup() throws IOException {
        Boolean fileCreated = createFileIfNeeded();
        if (fileCreated) {
            ConfigurationNode rootNode = loadRootNode();

            //Set up default item rewards
            ConfigurationNode itemNode = rootNode.getNode("item");
            @NonNull ConfigurationNode firstItem = itemNode.appendListNode();
            firstItem.getNode("name").setValue("pixelmon:rare_candy");
            firstItem.getNode("amount").setValue(2);
            @NonNull ConfigurationNode secondItem = itemNode.appendListNode();
            secondItem.getNode("name").setValue("pixelmon:ultra_ball");
            secondItem.getNode("amount").setValue(2);
            //

            //Set up default time interval
            ConfigurationNode timeNode = rootNode.getNode("time");
            timeNode.getNode("minimumTimeInterval").setValue(30);
            timeNode.getNode("maximumTimeInterval").setValue(35);
            //

            loader.save(rootNode);
        }
    }

    private Boolean createFileIfNeeded() throws IOException {
        File configFile = new File("config/whosthatpixelmon.conf");
        if (!configFile.exists()) {
            configFile.createNewFile();
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

    public int getMinTime() {
        return minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }
}
