package com.mikedeejay2.simplestack.api;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Simple Stack's timings information. Can be used to analyze Simple Stack's performance and toggle whether timings are
 * actively being collected.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
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
