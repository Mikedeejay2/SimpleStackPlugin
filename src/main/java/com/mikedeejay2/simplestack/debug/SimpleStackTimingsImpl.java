package com.mikedeejay2.simplestack.debug;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackTimings;

import java.text.DecimalFormat;
import java.util.*;

public final class SimpleStackTimingsImpl implements SimpleStackTimings {
    private final SimpleStack plugin;

    private boolean shouldCollect;
    private EvictingQueue<TimingEntry> detailedTimings;
    private EvictingQueue<Long> tickTimings;
    private DebugRunnable runnable;

    public SimpleStackTimingsImpl(SimpleStack plugin) {
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

    @Override
    public void startCollecting() {
        this.detailedTimings = EvictingQueue.create(500);
        this.tickTimings = EvictingQueue.create(18000); // 15 minutes
        this.runnable = new DebugRunnable(this);
        this.runnable.runTaskTimer(plugin);
        this.shouldCollect = true;
    }

    @Override
    public void stopCollecting() {
        this.shouldCollect = false;
        this.detailedTimings.clear();
        this.tickTimings.clear();
        this.runnable.cancel();
        this.detailedTimings = null;
        this.tickTimings = null;
        this.runnable = null;
    }

    @Override
    public boolean isCollecting() {
        return shouldCollect;
    }

    @Override
    public List<TimingEntry> getDetailedTimings() {
        return ImmutableList.copyOf(detailedTimings);
    }

    @Override
    public List<Long> getTickTimings() {
        return ImmutableList.copyOf(tickTimings);
    }

    @Override
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
                             SimpleStackTimings.getChatColorTotal(min), format.format(min),
                             SimpleStackTimings.getChatColorTotal(med), format.format(med),
                             SimpleStackTimings.getChatColorTotal(avg), format.format(avg),
                             SimpleStackTimings.getChatColorTotal(n95ile), format.format(n95ile),
                             SimpleStackTimings.getChatColorTotal(max), format.format(max));
    }

    @Override
    public List<String> getTimings() {
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
}
