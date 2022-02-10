package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ConfigManager {

    private enum ConfigOptions{
        SETUP,
        REWARDS,
        COMMANDS,
        REVEAL_ANSWER,
        TIME_INTERVALS,
        GUESSING_TIME,

        PREFIX,
        STARTING_MESSAGE,
        REVEALED_ANSWER_MESSAGE,
        NO_ANSWER_MESSAGE,
        GUESSED_MESSAGE,
        ITEM_RECEIVED_MESSAGE
    }

    private static ConfigManager instance;
    private final HashSet<ConfigOptions> loadedOptions = new HashSet<>();

    private boolean rewardsEnabled;
    private final HashMap<String, Integer> rewards = new HashMap<>();
    private boolean commandsEnabled;
    private ArrayList<String> commandsList;
    private boolean revealAnswer;
    private int minTime, maxTime;
    private int guessingTime;

    private String prefix;
    private String startingMessage;
    private String noAnswerMessage;
    private String revealedAnswerMessage;
    private String guessedMessage;
    private String itemReceivedMessage;

    public static ConfigManager getInstance() {
        if (instance != null) return instance;
        instance = new ConfigManager();
        return instance;
    }

    private ConfigurationNode loadRootNode() throws IOException {
        File configFile = new File("config/whosthatpixelmon/whosthatpixelmon.conf");
        return HoconConfigurationLoader.builder()
                .setFile(configFile)
                .build()
                .load();
    }

    private void initialSetup() {
        if (!fileExists()) {
            Asset defaultConfigAsset = WhosThatPixelmon.getInstance().getDefaultConfigAsset();
            try {
                defaultConfigAsset.copyToDirectory(WhosThatPixelmon.getInstance().getConfigPath());
            } catch (IOException e) {
                WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to create default config file");
            }
        }
        loadedOptions.add(ConfigOptions.SETUP);
    }

    private Boolean fileExists() {
        File configFile = new File("config/whosthatpixelmon/whosthatpixelmon.conf");
        return configFile.exists();
    }

    private void checkSetup() {
        if (!loadedOptions.contains(ConfigOptions.SETUP)) {
            initialSetup();
        }
    }

    public void loadRewards() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            rewardsEnabled = rootNode.getNode("itemsEnabled").getBoolean();
            if (!rewardsEnabled) return;

            List<? extends ConfigurationNode> list = rootNode.getNode("item").getChildrenList();
            for (ConfigurationNode node : list) {
                rewards.put(node.getNode("name").getString(), node.getNode("amount").getInt());
            }
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger()
                    .warn("[Whos that Pixelmon] itemsEnabled was unable to be read, defaulting to true");
        }
    }

    public void loadCommands() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            commandsEnabled = rootNode.getNode("commandsEnabled").getBoolean();
            if (!commandsEnabled) return;
            commandsList = new ArrayList<>();
            List<? extends ConfigurationNode> list = rootNode.getNode("commands").getChildrenList();
            for (ConfigurationNode node : list) {
                commandsList.add(node.getString());
            }
        } catch (IOException e) {
            commandsEnabled = false;
            commandsList = null;
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read one or more of the command nodes from config: 'commandsEnabled', 'commands'. The default value (commands disabled) will be used.");
        }
    }

    public void loadRevealAnswer() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            revealAnswer = rootNode.getNode("revealAnswer").getBoolean();
        } catch (IOException e) {
            revealAnswer = false;
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read the node 'revealAnswer' from config, the default value (false) will be used.");
        }
        loadedOptions.add(ConfigOptions.REVEAL_ANSWER);

    }

    public void loadTimeIntervals() {
        checkSetup();

        try {
            ConfigurationNode node = loadRootNode().getNode("time");
            minTime = node.getNode("minimumTimeInterval").getInt();
            maxTime = node.getNode("maximumTimeInterval").getInt();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read time intervals from config, the default values will be used.");
            minTime = 30;
            maxTime = 35;
        }
        loadedOptions.add(ConfigOptions.TIME_INTERVALS);
    }

    public void loadGuessingTime() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            guessingTime = rootNode.getNode("guessingTime").getInt();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read guessingTime from config, default value was used");
            guessingTime = 30;
        }
        loadedOptions.add(ConfigOptions.GUESSING_TIME);
    }

    public void loadPrefix() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            prefix = rootNode.getNode("prefix").getString();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read Prefix from config, default value was used");
            prefix = "[Chat Games]";
        }
        loadedOptions.add(ConfigOptions.PREFIX);
    }

    public void loadStartingMessage() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            startingMessage = rootNode.getNode("startingMessage").getString();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read startingMessage from config, default value was used");
            startingMessage = "'Whos that Pixelmon' will begin in 5 seconds. " +
                    "Have an empty main hand to participate";
        }
        loadedOptions.add(ConfigOptions.STARTING_MESSAGE);
    }

    public void loadNoAnswerMessage() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            noAnswerMessage = rootNode.getNode("noAnswerMessage").getString();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read noAnswerMessage from config, default value was used");
            noAnswerMessage = "Nobody guessed correctly in time";
        }
        loadedOptions.add(ConfigOptions.NO_ANSWER_MESSAGE);
    }

    public void loadRevealedAnswerMessage() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            revealedAnswerMessage = rootNode.getNode("revealedAnswerMessage").getString()+" ";
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read revealedAnswerMessage from config, default value was used");
            revealedAnswerMessage = "It's ";
        }
        loadedOptions.add(ConfigOptions.REVEALED_ANSWER_MESSAGE);
    }

    public void loadGuessedMessage() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            guessedMessage = rootNode.getNode("guessedMessage").getString()+". ";
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read guessedMessage from config, default value was used");
            guessedMessage = "guessed correctly. ";
        }
        loadedOptions.add(ConfigOptions.GUESSED_MESSAGE);
    }

    public void loadItemReceivedMessage() {
        checkSetup();

        try {
            ConfigurationNode rootNode = loadRootNode();
            itemReceivedMessage = rootNode.getNode("itemReceivedMessage").getString();
        } catch (IOException e) {
            WhosThatPixelmon.getInstance().getLogger().warn("[Whos that Pixelmon] Unable to read itemReceivedMessage from config, default value was used");
            itemReceivedMessage = "You have received";
        }
        loadedOptions.add(ConfigOptions.ITEM_RECEIVED_MESSAGE);
    }



    public boolean areRewardsEnabled() {
        if (loadedOptions.contains(ConfigOptions.REWARDS)) return rewardsEnabled;
        loadRewards();
        return rewardsEnabled;
    }

    public HashMap<String, Integer> getRewards() {
        if (loadedOptions.contains(ConfigOptions.REWARDS)) return rewards;
        loadRewards();
        return rewards;
    }

    public boolean areCommandsEnabled() {
        if (loadedOptions.contains(ConfigOptions.COMMANDS)) return commandsEnabled;
        loadCommands();
        return commandsEnabled;
    }

    public ArrayList<String> getCommandsList() {
        if (loadedOptions.contains(ConfigOptions.COMMANDS)) return commandsList;
        loadCommands();
        return commandsList;
    }

    public boolean shouldRevealAnswer() {
        if (loadedOptions.contains(ConfigOptions.REVEAL_ANSWER)) return revealAnswer;
        loadRevealAnswer();
        return revealAnswer;
    }

    public int getMinTime() {
        if (loadedOptions.contains(ConfigOptions.TIME_INTERVALS)) return minTime;
        loadTimeIntervals();
        return minTime;
    }

    public int getMaxTime() {
        if (loadedOptions.contains(ConfigOptions.TIME_INTERVALS)) return maxTime;
        loadTimeIntervals();
        return maxTime;
    }

    public int getGuessingTime() {
        if (loadedOptions.contains(ConfigOptions.GUESSING_TIME)) return guessingTime;
        loadGuessingTime();
        return guessingTime;
    }

    public String getPrefix() {
        if (loadedOptions.contains(ConfigOptions.PREFIX)) return prefix+" ";
        loadPrefix();
        return prefix+" ";
    }

    public String getStartingMessage() {
        if (loadedOptions.contains(ConfigOptions.STARTING_MESSAGE)) return startingMessage;
        loadStartingMessage();
        return startingMessage;
    }

    public String getNoAnswerMessage() {
        if (loadedOptions.contains(ConfigOptions.NO_ANSWER_MESSAGE)) return noAnswerMessage;
        loadNoAnswerMessage();
        return noAnswerMessage;
    }

    public String getRevealedAnswerMessage() {
        if (loadedOptions.contains(ConfigOptions.REVEALED_ANSWER_MESSAGE)) return revealedAnswerMessage;
        loadRevealedAnswerMessage();
        return revealedAnswerMessage;
    }

    public String getGuessedMessage() {
        if (loadedOptions.contains(ConfigOptions.GUESSED_MESSAGE)) return guessedMessage;
        loadGuessedMessage();
        return guessedMessage;
    }

    public String getItemReceivedMessage() {
        if (loadedOptions.contains(ConfigOptions.ITEM_RECEIVED_MESSAGE)) return itemReceivedMessage;
        loadItemReceivedMessage();
        return itemReceivedMessage;
    }
}
