package com.mikedeejay2.simplestack.api;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SimpleStackTimings {
    void startCollecting();
    void stopCollecting();
    boolean isCollecting();
    @NotNull List<TimingEntry> getDetailedTimings();
    @NotNull List<Long> getTickTimings();
    @NotNull String getTimingString(int ticks);
    @NotNull List<String> getTimings();

    interface TimingEntry {
        @NotNull String getName();
        long getNanoTime();
        long getMsTime();
        @NotNull ChatColor getChatColor();
    }
}
