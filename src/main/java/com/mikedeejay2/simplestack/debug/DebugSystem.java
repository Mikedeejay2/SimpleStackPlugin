package com.mikedeejay2.simplestack.debug;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

public final class DebugSystem {
    private final SimpleStack plugin;

    private boolean shouldCollect;
    private EvictingQueue<TimingEntry> detailedTimings;
    private EvictingQueue<Long> tickTimings;
    private DebugRunnable runnable;

    public DebugSystem(SimpleStack plugin) {
        this.plugin = plugin;
        this.shouldCollect = false;
        this.runnable = null;
        this.detailedTimings = null;
    }

    public void collect(long startTime, String name, boolean countTimings) {
        if(!shouldCollect) return;
        long endTime = System.nanoTime();
        long nanoTime = endTime - startTime;
        if(countTimings) this.runnable.collect(nanoTime);
        this.detailedTimings.add(new TimingEntry(name, nanoTime));
    }

    public void startCollecting() {
        this.detailedTimings = EvictingQueue.create(500);
        this.tickTimings = EvictingQueue.create(18000); // 15 minutes
        this.runnable = new DebugRunnable(this);
        this.runnable.runTaskTimer(plugin);
        this.shouldCollect = true;
    }

    public void stopCollecting() {
        this.shouldCollect = false;
        this.detailedTimings.clear();
        this.tickTimings.clear();
        this.runnable.cancel();
        this.detailedTimings = null;
        this.tickTimings = null;
        this.runnable = null;
    }

    public boolean isCollecting() {
        return shouldCollect;
    }

    public Set<TimingEntry> getDetailedTimings() {
        return ImmutableSet.copyOf(detailedTimings);
    }

    public List<Long> getTickTimings() {
        return ImmutableList.copyOf(tickTimings);
    }

    public static final class TimingEntry {
        public final String name;
        public final long nanoTime;
        public final ChatColor color;

        public TimingEntry(String name, long nanoTime) {
            this.name = name;
            this.nanoTime = nanoTime;
            if(nanoTime < 100000) {
                color = ChatColor.GREEN;
            } else if(nanoTime < 200000) {
                color = ChatColor.YELLOW;
            } else if(nanoTime < 500000) {
                color = ChatColor.RED;
            } else {
                color = ChatColor.DARK_RED;
            }
        }
    }

    private static final class DebugRunnable extends EnhancedRunnable {
        private final DebugSystem system;

        private long currentTick = 0;

        public DebugRunnable(DebugSystem system) {
            this.system = system;
        }

        public void collect(long time) {
            this.currentTick += time;
        }

        @Override
        public void onRun() {
            system.tickTimings.add(currentTick);
            currentTick = 0;
        }
    }
}
