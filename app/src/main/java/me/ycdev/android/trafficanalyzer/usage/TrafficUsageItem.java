package me.ycdev.android.trafficanalyzer.usage;

import me.ycdev.android.trafficanalyzer.stats.TagTrafficStats;

public class TrafficUsageItem {
    public TagTrafficStats usage;
    public boolean isTotal = false;

    public TrafficUsageItem(TagTrafficStats usage, boolean isTotal) {
        this.usage = usage;
        this.isTotal = isTotal;
    }
}
