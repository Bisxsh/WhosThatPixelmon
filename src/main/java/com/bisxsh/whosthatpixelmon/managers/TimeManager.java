package com.bisxsh.whosthatpixelmon.managers;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TimeManager {

    private static TimeManager INSTANCE = null;

    public static TimeManager getInstance() {
        return INSTANCE;
    }

    public TimeManager() {
        INSTANCE = this;
    }

    private int getTimeInterval() {
        ConfigManager configManager = new ConfigManager();
        configManager.loadTimeIntervals();
        int minTime = configManager.getMinTime()*60;
        int maxTime = configManager.getMaxTime()*60;

        if (minTime == maxTime) {
            return minTime;
        }

        Random random = new Random();
        return random.nextInt(maxTime-minTime)+minTime;
    }

    public void setChatGameTimer() {
        int timeInterval = getTimeInterval();

        Task.builder().delay(timeInterval, TimeUnit.SECONDS)
                .name("WhosThatPixelmon - Setting up timer for ChatGame").execute(
                        task -> {
                            try {
                                ChatGameManager chatGameManager = new ChatGameManager();
                                chatGameManager.startChatGame();
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                ).submit(WhosThatPixelmon.getInstance());
    }



}
