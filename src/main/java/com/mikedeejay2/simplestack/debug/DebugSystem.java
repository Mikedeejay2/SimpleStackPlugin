package com.mikedeejay2.simplestack.debug;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.simplestack.SimpleStack;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.*;

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
        long msTime = System.currentTimeMillis();
        long nanoTime = endTime - startTime;
        if(countTimings) this.runnable.collect(nanoTime);
        this.detailedTimings.add(new TimingEntry(name, nanoTime, msTime));
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

    public List<TimingEntry> getDetailedTimings() {
        return ImmutableList.copyOf(detailedTimings);
    }

    public List<Long> getTickTimings() {
        return ImmutableList.copyOf(tickTimings);
    }

    public String getTimingString(int ticks) {
        if(tickTimings.isEmpty()) return "N/A";
        ticks = Math.min(tickTimings.size(), ticks);

        Long[] arr = tickTimings.toArray(new Long[0]);
        Long[] trimmed = Arrays.copyOfRange(arr, Math.max(arr.length - ticks, 0), arr.length);
        List<Long> list = new ArrayList<>(Arrays.asList(trimmed));
        Collections.sort(list);

        double min = nsToMs(list.get(0));
        double med = nsToMs(list.get(list.size() % 2 == 0 ? (list.size() / 2) - 1 : (list.size() / 2)));
        long totalCount = 0;
        for(Long value : list) {
            totalCount += value;
        }
        double avg = nsToMs(totalCount) / (double) list.size();
        long n95ileCount = 0;
        int n95size = 0;
        for(int i = (int) (list.size() * 0.95); i < list.size(); ++i) {
            ++n95size;
            n95ileCount += list.get(i);
        }
        double n95ile = nsToMs(n95ileCount) / n95size;
        double max = nsToMs(list.get(list.size() - 1));


        DecimalFormat format = new DecimalFormat("0.0##");
        return String.format("%s%s&f/%s%s&f/%s%s&f/%s%s&f/%s%s",
                             getChatColorTotal(min), format.format(min),
                             getChatColorTotal(med), format.format(med),
                             getChatColorTotal(avg), format.format(avg),
                             getChatColorTotal(n95ile), format.format(n95ile),
                             getChatColorTotal(max), format.format(max));
    }

    private static double nsToMs(long nanos) {
        return (nanos / 1000000.0);
    }

    private static ChatColor getChatColorSingle(long nanoTime) {
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

    private static ChatColor getChatColorTotal(double ms) {
        return getChatColorSingle((long) (ms * 100000.0));
    }

    public static final class TimingEntry {
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
