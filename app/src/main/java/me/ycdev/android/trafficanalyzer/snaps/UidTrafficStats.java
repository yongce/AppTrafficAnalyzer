package me.ycdev.android.trafficanalyzer.snaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;

public class UidTrafficStats {
    private static final String TAG = "UidTrafficStats";
    private static final boolean DEBUG = AppLogger.DEBUG;

    public int uid;

    private TreeMap<TagTrafficStats.Type, TagTrafficStats> mAllTagsStats =
            new TreeMap<TagTrafficStats.Type, TagTrafficStats>();
    private TreeSet<String> mIfaceSet = new TreeSet<String>();

    public List<TagTrafficStats> getAllTagsStats() {
        return new ArrayList<TagTrafficStats>(mAllTagsStats.values());
    }

    public List<String> getAllIfaces() {
        return new ArrayList<String>(mIfaceSet);
    }

    /* package */
    void addTagTrafficStats(TagTrafficStats tagStats) throws StatsParseException {
        TagTrafficStats.Type key = new TagTrafficStats.Type(tagStats);
        if (mAllTagsStats.get(key) != null) {
            throw new StatsParseException("duplicate tag stats: " + tagStats.toString());
        }
        mAllTagsStats.put(key, tagStats);
        mIfaceSet.add(tagStats.iface);
    }

    public UidTrafficStats subtract(UidTrafficStats oldUidStats) {
        if (uid != oldUidStats.uid) {
            throw new IllegalArgumentException("not same uid");
        }

        UidTrafficStats uidUsage = new UidTrafficStats();
        uidUsage.uid = uid;
        for (Entry<TagTrafficStats.Type, TagTrafficStats> entry : mAllTagsStats.entrySet()) {
            TagTrafficStats.Type key = entry.getKey();
            TagTrafficStats tagStats = entry.getValue();
            TagTrafficStats oldTagStats = oldUidStats.mAllTagsStats.get(key);
            TagTrafficStats tagUsage = tagStats.subtract(oldTagStats);
            uidUsage.mAllTagsStats.put(key, tagUsage);
            uidUsage.mIfaceSet.add(tagUsage.iface);
            if (DEBUG) AppLogger.d(TAG, "tag usage: " + tagUsage);
        }

        if (DEBUG) {
            for (Entry<TagTrafficStats.Type, TagTrafficStats> entry : oldUidStats.mAllTagsStats.entrySet()) {
                if (!mAllTagsStats.containsKey(entry.getKey())) {
                    AppLogger.w(TAG, "unknown old tag stats: " + entry.getValue());
//                    throw new IllegalArgumentException("unknown old tag stats: " + entry.getValue());
                }
            }
            for (String iface : oldUidStats.mIfaceSet) {
                if (!mIfaceSet.contains(iface)) {
                    AppLogger.w(TAG, "unknown old iface: " + iface);
//                    throw new IllegalArgumentException("unknown old iface: " + iface);
                }
            }
        }

        return uidUsage;
    }

}
