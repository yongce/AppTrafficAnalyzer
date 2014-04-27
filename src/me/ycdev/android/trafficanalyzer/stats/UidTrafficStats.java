package me.ycdev.android.trafficanalyzer.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class UidTrafficStats {
    public int uid;

    private HashMap<TagTrafficStats, TagTrafficStats> mAllTagsStats =
            new HashMap<TagTrafficStats, TagTrafficStats>();
    private HashSet<String> mIfaceSet;

    public List<TagTrafficStats> getAllTagsStats() {
        return new ArrayList<TagTrafficStats>(mAllTagsStats.values());
    }

    public List<String> getAllIfaces() {
        return new ArrayList<String>(mIfaceSet);
    }

    void addTagTrafficStats(TagTrafficStats tagStats) throws StatsParseException {
        if (mAllTagsStats.get(tagStats) != null) {
            throw new StatsParseException("duplicate tag stats: " + tagStats.toString());
        }
        mAllTagsStats.put(tagStats, tagStats);
        mIfaceSet.add(tagStats.iface);
    }

    public UidTrafficStats subtract(UidTrafficStats oldUidStats) {
        if (uid != oldUidStats.uid) {
            throw new IllegalArgumentException("not same uid");
        }

        UidTrafficStats uidUsage = new UidTrafficStats();
        uidUsage.uid = uid;
        for (TagTrafficStats tagStats : mAllTagsStats.values()) {
            TagTrafficStats oldTagStats = oldUidStats.mAllTagsStats.get(tagStats);
            TagTrafficStats tagUsage = tagStats.subtract(oldTagStats);
            uidUsage.mAllTagsStats.put(tagUsage, tagUsage);
            uidUsage.mIfaceSet.add(tagUsage.iface);
        }
        return uidUsage;
    }

}
