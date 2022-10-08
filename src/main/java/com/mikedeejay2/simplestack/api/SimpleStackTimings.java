package com.mikedeejay2.simplestack.api;

import org.bukkit.ChatColor;

import java.util.List;

public interface SimpleStackTimings {
    void startCollecting();
    void stopCollecting();
    boolean isCollecting();
    List<TimingEntry> getDetailedTimings();
    List<Long> getTickTimings();
    String getTimingString(int ticks);
    List<String> getTimings();

    final class TimingEntry {
        public final String name;
        public final long nanoTime;
        public final long msTime;
        public final ChatColor color;

        public TimingEntry(String name, long nanoTime, long msTime) {
            this.name = name;
            this.nanoTime = nanoTime;
            this.msTime = msTime;
            color = getChatColorSingle(nanoTime);
        }
    }

    static ChatColor getChatColorSingle(long nanoTime) {
        ChatColor color;
        if(nanoTime < 10000) {
            color = ChatColor.GREEN;
        } else if(nanoTime < 50000) {
            color = ChatColor.YELLOW;
        } else if(nanoTime < 100000) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.DARK_RED;
        }
        return color;
    }

    static ChatColor getChatColorTotal(double ms) {
        return getChatColorSingle((long) (ms * 100000.0));
    }

}
