package com.mikedeejay2.simplestack.debug;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackTimings;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public final class SimpleStackTimingsImpl implements SimpleStackTimings {
    private final TimingsCollector noOpCollector;
    private final TimingsCollector activeCollector;
    private final SimpleStack plugin;

    private boolean isCollecting;
    private TimingsCollector timingsCollector;
    private EvictingQueue<TimingEntry> detailedTimings;
    private EvictingQueue<Long> tickTimings;
    private DebugRunnable runnable;

    public SimpleStackTimingsImpl(SimpleStack plugin) {
        this.plugin = plugin;
        this.isCollecting = false;
        this.noOpCollector = new NoOpTimingsCollector();
        this.activeCollector = new ActiveTimingsCollector(this);
        this.timingsCollector = noOpCollector;
        this.runnable = null;
        this.detailedTimings = null;
    }

    public void collect(long startTime, String name, boolean countTimings) {
        this.timingsCollector.collect(startTime, name, countTimings);
    }

    @Override
    public void startCollecting() {
        if(this.isCollecting) return;
        this.detailedTimings = EvictingQueue.create(500);
        this.tickTimings = EvictingQueue.create(18000); // 15 minutes
        this.runnable = new DebugRunnable(this);
        this.runnable.runTaskTimer(plugin);
        this.isCollecting = true;
        this.timingsCollector = activeCollector;
    }

    @Override
    public void stopCollecting() {
        if(!this.isCollecting) return;
        this.isCollecting = false;
        this.timingsCollector = noOpCollector;
        this.detailedTimings.clear();
        this.tickTimings.clear();
        this.runnable.cancel();
        this.detailedTimings = null;
        this.tickTimings = null;
        this.runnable = null;
    }

    @Override
    public boolean isCollecting() {
        return isCollecting;
    }

    @Override
    public @NotNull List<TimingEntry> getDetailedTimings() {
        return ImmutableList.copyOf(detailedTimings);
    }

    @Override
    public @NotNull List<Long> getTickTimings() {
        return ImmutableList.copyOf(tickTimings);
    }

    @Override
    public @NotNull String getTimingString(int ticks) {
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

    @Override
    public @NotNull List<String> getTimings() {
        return ImmutableList.of(
            "Ms per tick (min/med/avg/95%ile/max ms):",
            "5s:  " + getTimingString(100),
            "10s: " + getTimingString(200),
            "1m:  " + getTimingString(1200),
            "5m:  " + getTimingString(6000),
            "15m: " + getTimingString(18000));
    }

    private static double nsToMs(long nanos) {
        return (nanos / 1000000.0);
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

    private static final class TimingEntryImpl implements TimingEntry {
        public final String name;
        public final long nanoTime;
        public final long msTime;
        public final ChatColor color;

        public TimingEntryImpl(String name, long nanoTime, long msTime) {
            this.name = name;
            this.nanoTime = nanoTime;
            this.msTime = msTime;
            color = getChatColorSingle(nanoTime);
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public long getNanoTime() {
            return nanoTime;
        }

        @Override
        public long getMsTime() {
            return msTime;
        }

        @Override
        public @NotNull ChatColor getChatColor() {
            return color;
        }
    }

    private static final class DebugRunnable extends EnhancedRunnable {
        private final SimpleStackTimingsImpl system;

        private long currentTick = 0;

        public DebugRunnable(SimpleStackTimingsImpl system) {
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

    private interface TimingsCollector {
        void collect(long startTime, String name, boolean countTimings);
    }

    private static final class ActiveTimingsCollector implements TimingsCollector {
        private final SimpleStackTimingsImpl system;

        public ActiveTimingsCollector(SimpleStackTimingsImpl system) {
            this.system = system;
        }

        @Override
        public void collect(long startTime, String name, boolean countTimings) {
            long endTime = System.nanoTime();
            long msTime = endTime / 1000000;
            long nanoTime = endTime - startTime;
            if(countTimings) system.runnable.collect(nanoTime);
            system.detailedTimings.add(new TimingEntryImpl(name, nanoTime, msTime));
        }
    }

    private static final class NoOpTimingsCollector implements TimingsCollector {
        @Override
        public void collect(long startTime, String name, boolean countTimings) {}
    }
}
